package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.retrievers.InfoRetriever;
import de.rockware.pma.connector.common.utils.MapUtils;
import de.rockware.pma.connector.common.utils.OidGenerator;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.beans.TransferredRecipientsDetails;
import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class ExecutionStatusUpdaterLoadingStage implements LoadingStage {
  private final ExecutionContext executionContext;
  private final ExecutionStatusService executionStatusService;
  private final TransferredRecipientsService transferredRecipientsService;
  private final TransferredRecipientsDetails transferredRecipientsDetails;

  public ExecutionStatusUpdaterLoadingStage(
      ExecutionContext executionContext,
      ExecutionStatusService executionStatusService,
      TransferredRecipientsService transferredRecipientsService,
      TransferredRecipientsDetails transferredRecipientsDetails) {
    this.executionContext = executionContext;
    this.executionStatusService = executionStatusService;
    this.transferredRecipientsService = transferredRecipientsService;
    this.transferredRecipientsDetails = transferredRecipientsDetails;
  }

  @Override
  public void execute() {
    Data data = DataRetriever.getData(executionContext, Step.LOAD);
    if (Objects.isNull(data)) {
      log.debug("Null data: skipped");
      return;
    }
    InfoRetriever.getInfo(data).forEach(this::processInfo);
  }

  private void processInfo(Info info) {
    ExecutionStatusId id = new ExecutionStatusId(info.getCampaignId(), info.getDeliveryId());
    Date time = new Date();
    ExecutionStatus executionStatus = executionStatusService.get(id);
    if (Objects.isNull(executionStatus)) {
      executionStatus =
          new ExecutionStatus(
              id,
              info.getCampaignName(),
              info.getStartDate(),
              info.getEndDate(),
              info.getDeliveryName(),
              false,
              null,
              false,
              false,
              false,
              null,
              false,
              time);
    }
    executionStatus.update(info, time);
    executionStatusService.save(executionStatus);
    if (Objects.nonNull(transferredRecipientsDetails)
        && StringUtils.isNotEmpty(transferredRecipientsDetails.getCorrelationId())) {
      transferredRecipientsService.save(
          new TransferredRecipients(
              OidGenerator.generate(),
              MapUtils.putIfNotPresent(
                  executionContext.getDetailIdsCache(), id, OidGenerator.generate()),
              info.getCampaignId(),
              info.getDeliveryId(),
              transferredRecipientsDetails.getCorrelationId(),
              new Date(),
              transferredRecipientsDetails.getCount()));
    }
  }
}
