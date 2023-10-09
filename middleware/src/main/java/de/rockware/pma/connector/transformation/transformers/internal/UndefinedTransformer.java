package de.rockware.pma.connector.transformation.transformers.internal;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndefinedTransformer implements Transformer {
  @Override
  public void transform(ExecutionContext executionContext) {
    log.debug("Nothing to do");
  }
}
