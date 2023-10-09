package de.rockware.pma.connector.load.loaders.stages;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.beans.TransferredRecipientsDetails;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.internal.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingStageBuilder {

  private final RequestExecutor requestExecutor;
  private final ExecutionContext executionContext;
  private final String customerId;
  private final Info info;
  private final LoadingStageFactory executionStatusUpdaterLoadingStageFactory;
  private final List<LoadingStage> loadingStages = new ArrayList<>();

  private LoadingStageBuilder(
      RequestExecutor requestExecutor,
      ExecutionContext executionContext,
      String customerId,
      Info info,
      ExecutionStatusService executionStatusService,
      TransferredRecipientsService transferredRecipientsService) {
    this.requestExecutor = requestExecutor;
    this.executionContext = executionContext;
    this.customerId = customerId;
    this.info = info;
    this.executionStatusUpdaterLoadingStageFactory =
        d ->
            new ExecutionStatusUpdaterLoadingStage(
                executionContext, executionStatusService, transferredRecipientsService, d);
  }

  public LoadingStageBuilder addCreateCampaign() {
    loadingStages.add(
        wrap(
            new CreateCampaignLoadingStage(requestExecutor, executionContext, customerId, info),
            null));
    return this;
  }

  public LoadingStageBuilder addCreateDelivery() {
    loadingStages.add(
        wrap(
            new CreateDeliveryLoadingStage(requestExecutor, executionContext, customerId, info),
            null));
    return this;
  }

  public LoadingStageBuilder addCreateVariableDefinitions() {
    loadingStages.add(
        wrap(
            new CreateVariableDefinitionsLoadingStage(
                requestExecutor, executionContext, customerId, info),
            null));
    return this;
  }

  public LoadingStageBuilder addTransferRecipients(List<Record> records) {
    TransferredRecipientsDetails transferredRecipientsDetails = new TransferredRecipientsDetails();
    loadingStages.add(
        wrap(
            new TransferRecipientsLoadingStage(
                requestExecutor,
                executionContext,
                customerId,
                info,
                records,
                transferredRecipientsDetails),
            transferredRecipientsDetails));
    return this;
  }

  public LoadingStage build() {
    return new CompositeLoadingStage(loadingStages);
  }

  private LoadingStage wrap(
      LoadingStage loadingStage, TransferredRecipientsDetails transferredRecipientsDetails) {
    return new CompositeLoadingStage(
        Arrays.asList(
            loadingStage,
            executionStatusUpdaterLoadingStageFactory.create(transferredRecipientsDetails)));
  }

  public static LoadingStageBuilder create(
      RequestExecutor requestExecutor,
      ExecutionContext executionContext,
      String customerId,
      Info info,
      ExecutionStatusService executionStatusService,
      TransferredRecipientsService transferredRecipientsService) {
    return new LoadingStageBuilder(
        requestExecutor,
        executionContext,
        customerId,
        info,
        executionStatusService,
        transferredRecipientsService);
  }

  private interface LoadingStageFactory {
    LoadingStage create(TransferredRecipientsDetails transferredRecipientsDetails);
  }
}
