package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.configuration.beans.ConfigurationValidationResult;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.configuration.validators.ConfigurationValidator;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.ExecutionError;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.exceptions.ExecutionServiceException;
import de.rockware.pma.connector.execution.services.ExecutionCompletedService;
import de.rockware.pma.connector.execution.services.ExecutionService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.mail.services.MailSenderService;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionServiceImpl implements ExecutionService {

  private final ReadWriteService<Mapping> mappingService;
  private final ConfigurationService configurationService;
  private final MailSenderService mailSenderService;
  private final Factory<Extractor, ExecutionContext> extractorFactory;
  private final Factory<Transformer, ExecutionContext> transformerFactory;
  private final Factory<Loader, ExecutionContext> loaderFactory;
  private final ExecutionCompletedService executionCompletedService;

  @Autowired
  public ExecutionServiceImpl(
      ReadWriteService<Mapping> mappingService,
      ConfigurationService configurationService,
      MailSenderService mailSenderService,
      Factory<Extractor, ExecutionContext> extractorFactory,
      Factory<Transformer, ExecutionContext> transformerFactory,
      Factory<Loader, ExecutionContext> loaderFactory,
      ExecutionCompletedService executionCompletedService) {
    this.mappingService = mappingService;
    this.configurationService = configurationService;
    this.mailSenderService = mailSenderService;
    this.extractorFactory = extractorFactory;
    this.transformerFactory = transformerFactory;
    this.loaderFactory = loaderFactory;
    this.executionCompletedService = executionCompletedService;
  }

  @Override
  public void run() {
    run(Collections.emptyList(), Collections.emptyList());
  }

  @Override
  public void run(List<Info> infoList, List<String> additionalMailRecipients) {
    try {
      Date start = new Date();
      log.debug("Execution start");
      Collection<Mapping> mappings = mappingService.getAll();
      if (Objects.isNull(mappings) || mappings.isEmpty()) {
        throw new ExecutionServiceException("No mapping defined");
      }
      Collection<ConfigurationValue> configurationValues = configurationService.getAll();
      ConfigurationValidationResult configurationValidationResult =
          ConfigurationValidator.validate(configurationValues);
      if (!configurationValidationResult.isValid()) {
        throw new ExecutionServiceException(
            String.format("Invalid configuration: %s", configurationValidationResult.getMessage()));
      }
      ExecutionContext executionContext =
          new ExecutionContext(mappings, configurationValues, new Date());
      if (Objects.nonNull(infoList)) {
        infoList.stream()
            .filter(Objects::nonNull)
            .forEach(i -> executionContext.getInitialInfoList().add(i));
      }
      List<ExecutionError> executionErrors = new ArrayList<>();
      executeStep(
          c -> true,
          Step.EXTRACTION,
          executionContext,
          ConfigurationKey.EXTRACTION_TYPE,
          c -> extractorFactory.create(executionContext),
          Extractor::extract,
          executionErrors,
          c -> c.getExtractedData().getRecords().size());
      executeStep(
          c -> !executionContext.getExtractedData().getRecords().isEmpty(),
          Step.TRANSFORMATION,
          executionContext,
          ConfigurationKey.TRANSFORMATION_TYPE,
          c -> transformerFactory.create(executionContext),
          Transformer::transform,
          executionErrors,
          c -> c.getTransformedData().getRecords().size());
      executeStep(
          c -> !executionContext.getTransformedData().getRecords().isEmpty(),
          Step.LOAD,
          executionContext,
          ConfigurationKey.LOAD_TYPE,
          c -> loaderFactory.create(executionContext),
          Loader::load,
          executionErrors,
          c -> c.getLoadedData().getRecords().size());
      log.debug(
          String.format(
              "Execution completed in %d millis", new Date().getTime() - start.getTime()));
      executionCompletedService.process(executionContext);
      if (!executionErrors.isEmpty()) {
        log.debug("Sending email with execution errors");
        mailSenderService.send(
            "Execution completed with errors", additionalMailRecipients, executionErrors);
      }
    } catch (Throwable e) {
      String message =
          String.format("Something unexpected happened during execution: %s", e.getMessage());
      log.error(message, e);
      mailSenderService.send("Execution error", additionalMailRecipients, message, e);
    }
  }

  private <T> void executeStep(
      Predicate<ExecutionContext> executeStepPredicate,
      @NonNull Step step,
      ExecutionContext executionContext,
      ConfigurationKey key,
      Function<ExecutionContext, T> stepFactory,
      BiConsumer<T, ExecutionContext> stepRunner,
      List<ExecutionError> executionErrors,
      Function<ExecutionContext, Integer> processedRecordsFactory) {
    if (!executeStepPredicate.test(executionContext)) {
      log.debug(String.format("%s step skipped", step));
      return;
    }
    String stepName = step.name();
    log.debug(String.format("Beginning %s", stepName));
    T stepExecutor = stepFactory.apply(executionContext);
    if (Objects.isNull(stepExecutor)) {
      throw new ExecutionServiceException(
          String.format(
              "No %s step executor created for %s",
              stepName,
              ConfigurationValueRetriever.get(executionContext.getConfigurationValues(), key)));
    }
    log.debug(
        String.format(
            "%s step executor created: %s", stepName, stepExecutor.getClass().getSimpleName()));
    stepRunner.accept(stepExecutor, executionContext);
    log.debug(
        String.format(
            "%s step executed successfully, processed %d records",
            step, processedRecordsFactory.apply(executionContext)));
    Data data = DataRetriever.getData(executionContext, step);
    if (Objects.nonNull(data) && !data.getErrors().isEmpty()) {
      executionErrors.addAll(data.getErrors());
    }
  }
}
