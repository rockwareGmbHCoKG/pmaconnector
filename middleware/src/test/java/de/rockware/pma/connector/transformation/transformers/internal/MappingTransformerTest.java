package de.rockware.pma.connector.transformation.transformers.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createExecutionContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.transformation.exception.TransformerException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MappingTransformerTest {

  private MappingTransformer sut;

  @BeforeEach
  void setUp() {
    this.sut = new MappingTransformer();
  }

  @Test
  void nullExecutionContext() {
    assertThrows(TransformerException.class, () -> sut.transform(null));
  }

  @Test
  void noExtractedData() {
    ExecutionContext executionContext = createExecutionContext();
    sut.transform(executionContext);
    assertEquals(0, executionContext.getTransformedData().getRecords().size());
  }

  @Test
  void skippedFields() {
    ExecutionContext executionContext = createExecutionContext();
    IntStream.rangeClosed(1, 10)
        .forEach(
            i ->
                executionContext
                    .getExtractedData()
                    .getRecords()
                    .add(createExtractedRow(executionContext.getMappings(), i, true)));
    sut.transform(executionContext);
    assertEquals(10, executionContext.getTransformedData().getRecords().size());
  }

  private Record createExtractedRow(
      Collection<Mapping> mappings, int index, boolean notExistingFields) {
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
    for (Mapping mapping : mappings) {
      record
          .getValues()
          .add(
              new Value(mapping.getSource(), String.format("Value %d %d", mapping.getId(), index)));
    }
    if (notExistingFields) {
      for (int i = 1; i < 4; i++) {
        record
            .getValues()
            .add(
                new Value(
                    String.format("Not mapped field %d", i),
                    String.format("Not mapped value %d %d", i, index)));
      }
    }
    return record;
  }
}
