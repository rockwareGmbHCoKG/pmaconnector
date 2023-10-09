package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.converters.ExceptionConverter;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.retrievers.InfoRetriever;
import de.rockware.pma.connector.common.utils.DurationCalculator;
import de.rockware.pma.connector.common.utils.MapUtils;
import de.rockware.pma.connector.common.utils.OidGenerator;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.*;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.entities.ExecutionStatusDetails;
import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.exceptions.CleanupServiceException;
import de.rockware.pma.connector.execution.services.ExecutionCompletedService;
import de.rockware.pma.connector.execution.services.ExecutionStatusDetailsService;
import de.rockware.pma.connector.execution.services.ExecutionStatusErrorService;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionCompletedServiceImpl implements ExecutionCompletedService {
  private final Factory<ChannelAdapter, Collection<ConfigurationValue>> channelAdapterFactory;
  private final ExecutionStatusErrorService executionStatusErrorService;
  private final ExecutionStatusDetailsService executionStatusDetailsService;

  @Autowired
  public ExecutionCompletedServiceImpl(
      Factory<ChannelAdapter, Collection<ConfigurationValue>> channelAdapterFactory,
      ExecutionStatusErrorService executionStatusErrorService,
      ExecutionStatusDetailsService executionStatusDetailsService) {
    this.channelAdapterFactory = channelAdapterFactory;
    this.executionStatusErrorService = executionStatusErrorService;
    this.executionStatusDetailsService = executionStatusDetailsService;
  }

  @Override
  public void process(ExecutionContext executionContext) {
    if (Objects.isNull(executionContext)) {
      throw new CleanupServiceException("Null execution context");
    }
    if (Objects.isNull(executionContext.getConfigurationValues())
        || executionContext.getConfigurationValues().isEmpty()) {
      throw new CleanupServiceException("Empty configuration values");
    }
    if (executionContext.isEmpty()) {
      return;
    }
    try {
      Date time = new Date();
      Collection<ConfigurationValue> configurationValues =
          executionContext.getConfigurationValues();
      ChannelAdapter channelAdapter = channelAdapterFactory.create(configurationValues);
      Set<String> renamedResourcesOldNames = new HashSet<>();
      HashSet<ExecutionStatusDetails> details = new HashSet<>();
      processErrors(
          executionContext,
          channelAdapter,
          configurationValues,
          renamedResourcesOldNames,
          details,
          time);
      if (!renamedResourcesOldNames.isEmpty()) {
        log.debug(String.format("Resources renamed: %s", renamedResourcesOldNames));
      }
      processRecords(
          executionContext,
          renamedResourcesOldNames,
          channelAdapter,
          configurationValues,
          details,
          time);
      details.forEach(executionStatusDetailsService::save);
    } catch (Throwable e) {
      String message = "Error cleaning up after execution: %s";
      log.error(String.format(message, e.getMessage()), e);
      throw new CleanupServiceException(message, e);
    }
  }

  private void processErrors(
      ExecutionContext executionContext,
      ChannelAdapter channelAdapter,
      Collection<ConfigurationValue> configurationValues,
      Set<String> renamedResourcesOldNames,
      Set<ExecutionStatusDetails> details,
      Date time) {
    List<ResourceWithErrors> resourcesWithErrors = new ArrayList<>();
    processStepErrors(
        resourcesWithErrors,
        executionContext.getExtractedData().getErrors(),
        renamedResourcesOldNames,
        time);
    processStepErrors(
        resourcesWithErrors,
        executionContext.getTransformedData().getErrors(),
        renamedResourcesOldNames,
        time);
    processStepErrors(
        resourcesWithErrors,
        executionContext.getLoadedData().getErrors(),
        renamedResourcesOldNames,
        time);
    resourcesWithErrors.forEach(
        r ->
            processResourceWithErrors(
                channelAdapter,
                configurationValues,
                executionContext.getDetailIdsCache(),
                r,
                time));
    InfoRetriever.getInfoFromErrors(
            resourcesWithErrors.stream()
                .flatMap(r -> r.getExecutionErrors().stream())
                .collect(Collectors.toList()))
        .stream()
        .map(i -> new ExecutionStatusId(i.getCampaignId(), i.getDeliveryId()))
        .distinct()
        .forEach(
            i ->
                addExecutionDetails(
                    executionContext, time, executionContext.getDetailIdsCache(), i, details));
  }

  private void processStepErrors(
      List<ResourceWithErrors> resourceWithErrors,
      Collection<ExecutionError> executionErrors,
      Set<String> renamedResourcesOldNames,
      Date time) {
    for (ExecutionError executionError : executionErrors) {
      String resourceName =
          Optional.ofNullable(executionError.getInfo()).map(Info::getResourceName).orElse(null);
      if (StringUtils.isEmpty(resourceName)) {
        continue;
      }
      String newResourceName =
          String.format(
              "%s.%d.error", resourceName, Optional.ofNullable(time).map(Date::getTime).orElse(0L));
      executionError.setNewResourceName(newResourceName);
      renamedResourcesOldNames.add(resourceName);
      ResourceWithErrors currentResourceWithErrors =
          resourceWithErrors.stream()
              .filter(r -> r.getOldName().equals(resourceName))
              .findFirst()
              .orElse(null);
      if (Objects.isNull(currentResourceWithErrors)) {
        currentResourceWithErrors = new ResourceWithErrors(resourceName, newResourceName);
        resourceWithErrors.add(currentResourceWithErrors);
      }
      currentResourceWithErrors.getExecutionErrors().add(executionError);
    }
  }

  private void processResourceWithErrors(
      ChannelAdapter channelAdapter,
      Collection<ConfigurationValue> configurationValues,
      Map<ExecutionStatusId, String> detailIdsCache,
      ResourceWithErrors resourceWithErrors,
      Date time) {
    saveErrors(resourceWithErrors.getExecutionErrors(), detailIdsCache, time);
    if (ConfigurationValueRetriever.getAsBoolean(
        configurationValues, ConfigurationKey.ERROR_KEEP_FILES)) {
      channelAdapter.rename(
          configurationValues, resourceWithErrors.getOldName(), resourceWithErrors.getNewName());
    }
    if (ConfigurationValueRetriever.getAsBoolean(
        configurationValues, ConfigurationKey.ERROR_WRITE_DETAILS)) {
      writeErrorFile(
          channelAdapter,
          configurationValues,
          resourceWithErrors.getOldName(),
          resourceWithErrors.getExecutionErrors(),
          time);
    }
  }

  private void saveErrors(
      Set<ExecutionError> executionErrors,
      Map<ExecutionStatusId, String> detailIdsCache,
      Date time) {
    if (Objects.isNull(executionErrors) || executionErrors.isEmpty()) {
      return;
    }
    for (ExecutionError executionError : executionErrors) {
      Info info = Optional.ofNullable(executionError.getInfo()).orElse(Constants.UNKNOWN);
      String campaignId = info.getCampaignId();
      String deliveryId = info.getDeliveryId();
      ExecutionStatusId campaignAndDelivery = new ExecutionStatusId(campaignId, deliveryId);
      executionStatusErrorService.save(
          new ExecutionStatusError(
              OidGenerator.generate(),
              MapUtils.putIfNotPresent(
                  detailIdsCache, campaignAndDelivery, OidGenerator.generate()),
              campaignId,
              deliveryId,
              executionError.getStep().name(),
              info.getResourceName(),
              executionError.getNewResourceName(),
              executionError.getMessage(),
              ExceptionConverter.convert(executionError.getThrowable()),
              time));
    }
  }

  private void writeErrorFile(
      ChannelAdapter channelAdapter,
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      Set<ExecutionError> executionErrors,
      Date time) {
    File tempFile = null;
    try {
      tempFile = Files.createTempFile("error", "txt").toFile();
      FileWriter writer = new FileWriter(tempFile);
      writer.write(
          String.format("Error during execution for resource file \"%s\"\n", resourceName));
      AtomicInteger counter = new AtomicInteger(0);
      for (ExecutionError e : executionErrors) {
        writer.write(
            String.format(
                "%d. Step: %s\n\tMessage: %s\n",
                counter.incrementAndGet(), e.getStep(), e.getMessage()));
      }
      writer.close();
      String errorFileResourceName =
          String.format(
              "%s.%d.error.details",
              resourceName, Optional.ofNullable(time).map(Date::getTime).orElse(0L));
      channelAdapter.write(configurationValues, errorFileResourceName, tempFile);
    } catch (Throwable e) {
      log.error(String.format("Error writing error file: %s", e.getMessage()), e);
    } finally {
      if (Objects.nonNull(tempFile) && tempFile.exists()) {
        log.debug(String.format("Error temporary file deleted: %s", tempFile.delete()));
      }
    }
  }

  private void processRecords(
      ExecutionContext executionContext,
      Set<String> renamedResourcesOldNames,
      ChannelAdapter channelAdapter,
      Collection<ConfigurationValue> configurationValues,
      HashSet<ExecutionStatusDetails> details,
      Date time) {
    Set<String> resourcesToBeDeleted = new HashSet<>();
    Set<ExecutionStatusId> campaignsAndDeliveries = new HashSet<>();
    processStepRecords(
        executionContext.getExtractedData(),
        renamedResourcesOldNames,
        resourcesToBeDeleted,
        campaignsAndDeliveries);
    processStepRecords(
        executionContext.getTransformedData(),
        renamedResourcesOldNames,
        resourcesToBeDeleted,
        campaignsAndDeliveries);
    processStepRecords(
        executionContext.getLoadedData(),
        renamedResourcesOldNames,
        resourcesToBeDeleted,
        campaignsAndDeliveries);
    resourcesToBeDeleted.forEach(n -> channelAdapter.delete(configurationValues, n));
    log.debug(String.format("Resources deleted: %s", resourcesToBeDeleted));
    campaignsAndDeliveries.forEach(
        c ->
            addExecutionDetails(
                executionContext, time, executionContext.getDetailIdsCache(), c, details));
  }

  private void processStepRecords(
      Data data,
      Set<String> renamedResourcesOldNames,
      Set<String> resourcesToBeDeleted,
      Set<ExecutionStatusId> campaignsAndDeliveries) {
    List<Info> infoList =
        data.getRecords().stream().map(Record::getInfo).distinct().collect(Collectors.toList());
    for (Info info : infoList) {
      if (Objects.isNull(info)) {
        continue;
      }
      String resourceName = info.getResourceName();
      if (StringUtils.isEmpty(resourceName)) {
        continue;
      }
      if (renamedResourcesOldNames.contains(resourceName)) {
        continue;
      }
      resourcesToBeDeleted.add(resourceName);
      campaignsAndDeliveries.add(new ExecutionStatusId(info.getCampaignId(), info.getDeliveryId()));
    }
  }

  private void addExecutionDetails(
      ExecutionContext executionContext,
      Date time,
      Map<ExecutionStatusId, String> detailIdsCache,
      ExecutionStatusId campaignAndDelivery,
      Set<ExecutionStatusDetails> details) {
    Date startTime = Optional.ofNullable(executionContext.getStartTime()).orElse(null);
    details.add(
        new ExecutionStatusDetails(
            MapUtils.putIfNotPresent(detailIdsCache, campaignAndDelivery, OidGenerator.generate()),
            campaignAndDelivery.getCampaignId(),
            campaignAndDelivery.getDeliveryId(),
            getMessagesList(executionContext, Step.EXTRACTION),
            getMessagesList(executionContext, Step.TRANSFORMATION),
            getMessagesList(executionContext, Step.LOAD),
            startTime,
            time,
            DurationCalculator.calculate(startTime, time)));
  }

  private List<String> getMessagesList(ExecutionContext executionContext, Step step) {
    Data data = DataRetriever.getData(executionContext, step);
    if (Objects.isNull(data)) {
      return null;
    }
    List<String> messages =
        data.getRecords().stream()
            .flatMap(r -> r.getMessages().stream())
            .collect(Collectors.toList());
    if (messages.isEmpty()) {
      return null;
    }
    return messages;
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  private static class ResourceWithErrors {
    private final String oldName;
    private final String newName;
    private final Set<ExecutionError> executionErrors = new HashSet<>();

    private ResourceWithErrors(String oldName, String newName) {
      this.oldName = oldName;
      this.newName = newName;
    }
  }
}
