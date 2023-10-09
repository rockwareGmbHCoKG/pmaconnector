package de.rockware.pma.connector.load.loaders.internal;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.beans.AuthenticationBody;
import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.common.utils.Partition;
import de.rockware.pma.connector.common.utils.TokenDecoder;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.exceptions.LoaderException;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.LoadingStageBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PmaRestLoader implements Loader {

  private final RequestExecutor requestExecutor;
  private final ExecutionStatusService executionStatusService;
  private final TransferredRecipientsService transferredRecipientsService;

  public PmaRestLoader(
      RequestExecutor requestExecutor,
      ExecutionStatusService executionStatusService,
      TransferredRecipientsService transferredRecipientsService) {
    this.requestExecutor = requestExecutor;
    this.executionStatusService = executionStatusService;
    this.transferredRecipientsService = transferredRecipientsService;
  }

  @Override
  public void load(ExecutionContext executionContext) {
    try {
      Data data = DataRetriever.getData(executionContext, Step.TRANSFORMATION);
      if (Objects.isNull(data) || data.getRecords().isEmpty()) {
        log.debug("No data to be loaded: skipped");
        return;
      }
      Collection<ConfigurationValue> configurationValues =
          executionContext.getConfigurationValues();
      AuthenticationBody authenticationBody =
          new AuthenticationBody(
              ConfigurationValueRetriever.get(
                  configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_ID),
              ConfigurationValueRetriever.get(
                  configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_CUSTOMER_ID),
              ConfigurationValueRetriever.get(
                  configurationValues, ConfigurationKey.PMA_AUTHENTICATION_SECRET),
              ConfigurationValueRetriever.get(
                  configurationValues, ConfigurationKey.PMA_AUTHENTICATION_LOCALE));
      String jwtToken = requestExecutor.authenticate(configurationValues, authenticationBody);
      Token token = TokenDecoder.decode(jwtToken);
      if (Objects.isNull(token)
          || Objects.isNull(token.getCustomerIds())
          || token.getCustomerIds().length == 0) {
        throw new RuntimeException("Error: invalid token");
      }
      data.getRecords().stream()
          .filter(r -> Objects.nonNull(r) && Objects.nonNull(r.getInfo()))
          .collect(
              Collectors.groupingBy(
                  r -> r.getInfo().getCampaignId() + "-" + r.getInfo().getCampaignId()))
          .forEach((k, v) -> processRecords(executionContext, requestExecutor, token, v));
    } catch (Throwable e) {
      throw new LoaderException(String.format("Error performing load: %s", e.getMessage()), e);
    }
  }

  private void processRecords(
      ExecutionContext executionContext,
      RequestExecutor requestExecutor,
      Token token,
      List<Record> transformedRecords) {
    Info info = Constants.UNKNOWN;
    try {
      if (Objects.isNull(transformedRecords) || transformedRecords.isEmpty()) {
        log.debug("Empty records");
        return;
      }
      Record transformed = transformedRecords.get(0);
      if (Objects.isNull(transformed) || Objects.isNull(transformed.getInfo())) {
        log.debug(String.format("Invalid record: %s", transformed));
        return;
      }
      info = transformed.getInfo().copy();
      String customerId = token.getCustomerIds()[0];
      LoadingStageBuilder loadingStageBuilder =
          LoadingStageBuilder.create(
              requestExecutor,
              executionContext,
              customerId,
              info,
              executionStatusService,
              transferredRecipientsService);
      loadingStageBuilder.addCreateCampaign();
      loadingStageBuilder.addCreateDelivery();
      loadingStageBuilder.addCreateVariableDefinitions();
      if (!info.isProof()) {
        Partition.ofSize(transformedRecords, 100)
            .forEach(loadingStageBuilder::addTransferRecipients);
      }
      loadingStageBuilder.build().execute();
    } catch (Throwable e) {
      String message = String.format("Error processing records: %s", e.getMessage());
      log.error(message, e);
      DataUpdater.addError(executionContext, info, Step.LOAD, message, e);
    }
  }
}
