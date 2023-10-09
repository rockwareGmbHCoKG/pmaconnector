package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.beans.TransferRecipients;
import de.rockware.pma.connector.common.beans.TransferRecipientsResult;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.load.beans.TransferredRecipientsDetails;
import de.rockware.pma.connector.load.exceptions.LoadingStageException;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransferRecipientsLoadingStage implements LoadingStage {
  private final RequestExecutor requestExecutor;
  private final ExecutionContext executionContext;
  private final String customerId;
  private final Info info;
  private final List<Record> records;
  private final TransferredRecipientsDetails transferredRecipientsDetails;

  public TransferRecipientsLoadingStage(
      RequestExecutor requestExecutor,
      ExecutionContext executionContext,
      String customerId,
      Info info,
      List<Record> records,
      TransferredRecipientsDetails transferredRecipientsDetails) {
    this.requestExecutor = requestExecutor;
    this.executionContext = executionContext;
    this.customerId = customerId;
    this.info = info;
    this.records = records;
    this.transferredRecipientsDetails = transferredRecipientsDetails;
  }

  @Override
  public void execute() {
    if (records.isEmpty()) {
      return;
    }
    if (!info.isCampaignStarted()) {
      throw new LoadingStageException(
          String.format(
              "Campaign %d is not active: impossible to transfer recipients",
              info.getCreatedCampaignId()));
    }
    TransferRecipients transferRecipients = new TransferRecipients();
    transferRecipients.setCampaignId(info.getCreatedCampaignId());
    transferRecipients.setCustomerId(customerId);
    List<TransferRecipients.Recipients> recipients = new ArrayList<>();
    for (Record record : records) {
      List<TransferRecipients.RecipientData> recipientData = new ArrayList<>();
      for (Value value : record.getValues()) {
        recipientData.add(new TransferRecipients.RecipientData(value.getField(), value.getValue()));
      }
      recipients.add(new TransferRecipients.Recipients(recipientData, null));
    }
    transferRecipients.setRecipients(recipients);
    TransferRecipientsResult transferRecipientsResult =
        requestExecutor.transferRecipients(
            executionContext.getConfigurationValues(), transferRecipients);
    if (Objects.nonNull(transferRecipientsResult)) {
      transferredRecipientsDetails.setCorrelationId(transferRecipientsResult.getCorrelationId());
      transferredRecipientsDetails.setCount(records.size());
      DataUpdater.addMessage(
          executionContext,
          Step.LOAD,
          info,
          String.format(
              "Recipients successfully transferred, PMA correlation ID: %s",
              transferRecipientsResult.getCorrelationId()));
    }
  }
}
