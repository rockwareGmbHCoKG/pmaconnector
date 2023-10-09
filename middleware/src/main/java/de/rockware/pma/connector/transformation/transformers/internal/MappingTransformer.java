package de.rockware.pma.connector.transformation.transformers.internal;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.MappingRetriever;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.transformation.exception.TransformerException;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import de.rockware.pma.connector.transformation.utils.LeadingAndTrailingQuotesRemover;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MappingTransformer implements Transformer {

  @Override
  public void transform(ExecutionContext executionContext) {
    try {
      Data extractedData = executionContext.getExtractedData();
      extractedData.getRecords().forEach(e -> transformRecord(e, executionContext));
      log.debug("Execution completed");
    } catch (Throwable e) {
      throw new TransformerException(
          String.format("Error performing transformation: %s", e.getMessage()), e);
    }
  }

  private void transformRecord(Record extracted, ExecutionContext executionContext) {
    Collection<Mapping> mappings = executionContext.getMappings();
    Record record = new Record(extracted.getInfo());
    for (Value value : extracted.getValues()) {
      String sourceField = value.getField();
      Mapping mapping = MappingRetriever.getBySourceField(mappings, sourceField);
      String valueWithoutQuotes = LeadingAndTrailingQuotesRemover.remove(value.getValue());
      if (Objects.isNull(mapping)) {
        record.getValues().add(new Value(sourceField, valueWithoutQuotes));
      } else {
        record.getValues().add(new Value(mapping.getTarget(), valueWithoutQuotes));
      }
    }
    DataUpdater.addRecord(executionContext, Step.TRANSFORMATION, record);
  }
}
