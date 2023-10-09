package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestExtractor implements Extractor {

  @Override
  public void extract(ExecutionContext executionContext) {
    IntStream.range(0, 50)
        .forEach(i -> addTestRecord(i, executionContext.getMappings(), executionContext));
    log.debug("Execution completed");
  }

  private void addTestRecord(
      int index, Collection<Mapping> mappings, ExecutionContext executionContext) {
    Info info = new Info();
    info.setCampaignId("TestCampaignId");
    info.setCampaignName("Test campaign id name");
    info.setStartDate(
        Date.from(
            LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    info.setEndDate(
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    Record record = new Record(info);
    mappings.forEach(
        m ->
            record
                .getValues()
                .add(new Value(m.getSource(), String.format("TEST_VALUE_%d", index))));
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, record);
  }
}
