package de.rockware.pma.connector.common.retrievers;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createExecutionContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import org.junit.jupiter.api.Test;

class DataRetrieverTest {

  @Test
  void nullExecutionContext() {
    assertNull(DataRetriever.getData(null, Step.EXTRACTION));
  }

  @Test
  void nullStep() {
    assertNull(DataRetriever.getData(createExecutionContext(), null));
  }

  @Test
  void extraction() {
    ExecutionContext executionContext = createExecutionContext();
    executionContext.getExtractedData().getRecords().add(new Record(new Info()));
    assertEquals(
        executionContext.getExtractedData(),
        DataRetriever.getData(executionContext, Step.EXTRACTION));
  }

  @Test
  void transformation() {
    ExecutionContext executionContext = createExecutionContext();
    executionContext.getTransformedData().getRecords().add(new Record(new Info()));
    assertEquals(
        executionContext.getTransformedData(),
        DataRetriever.getData(executionContext, Step.TRANSFORMATION));
  }

  @Test
  void load() {
    ExecutionContext executionContext = createExecutionContext();
    executionContext.getLoadedData().getRecords().add(new Record(new Info()));
    assertEquals(
        executionContext.getLoadedData(), DataRetriever.getData(executionContext, Step.LOAD));
  }
}
