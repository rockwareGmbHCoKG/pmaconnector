package de.rockware.pma.connector.common.retrievers;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import java.util.Objects;

public class DataRetriever {

  public static Data getData(ExecutionContext executionContext, Step step) {
    if (Objects.isNull(executionContext)) {
      return null;
    }
    if (Step.EXTRACTION.equals(step)) {
      return executionContext.getExtractedData();
    }
    if (Step.TRANSFORMATION.equals(step)) {
      return executionContext.getTransformedData();
    }
    if (Step.LOAD.equals(step)) {
      return executionContext.getLoadedData();
    }
    return null;
  }
}
