package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.retrievers.InfoRetriever;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionStatusUpdaterExtractor implements Extractor {
  private final ExecutionStatusService executionStatusService;

  public ExecutionStatusUpdaterExtractor(ExecutionStatusService executionStatusService) {
    this.executionStatusService = executionStatusService;
  }

  @Override
  public void extract(ExecutionContext executionContext) {
    if (Objects.isNull(executionContext)) {
      log.debug("Null execution context");
      return;
    }
    Data data = DataRetriever.getData(executionContext, Step.EXTRACTION);
    if (Objects.isNull(data)) {
      log.debug("Null data: skipped");
      return;
    }
    for (Info info : InfoRetriever.getInfo(data)) {
      try {
        ExecutionStatusId id = new ExecutionStatusId(info.getCampaignId(), info.getDeliveryId());
        ExecutionStatus executionStatus = executionStatusService.get(id);
        info.update(executionStatus);
        Date time = new Date();
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
        } else {
          executionStatus.setLastExecutionTime(time);
        }
        executionStatusService.save(executionStatus);
      } catch (Throwable e) {
        log.error(String.format("Error processing info %s: %s", info, e.getMessage()), e);
      }
    }
  }
}
