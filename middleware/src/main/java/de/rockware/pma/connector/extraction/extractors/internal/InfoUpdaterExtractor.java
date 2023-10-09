package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoUpdaterExtractor implements Extractor {
  private final ExecutionStatusService executionStatusService;

  public InfoUpdaterExtractor(ExecutionStatusService executionStatusService) {
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
    Map<ExecutionStatusId, Boolean> cache = new HashMap<>();
    executionContext.getExtractedData().getRecords().stream()
        .filter(Objects::nonNull)
        .map(Record::getInfo)
        .filter(Objects::nonNull)
        .forEach(i -> updateInfo(i, cache));
  }

  private void updateInfo(Info info, Map<ExecutionStatusId, Boolean> cache) {
    ExecutionStatusId id = new ExecutionStatusId(info.getCampaignId(), info.getDeliveryId());
    Boolean proof = cache.get(id);
    if (Objects.isNull(proof)) {
      ExecutionStatus executionStatus = executionStatusService.get(id);
      cache.put(id, Objects.isNull(executionStatus));
      proof = cache.get(id);
    }
    info.setProof(proof);
  }
}
