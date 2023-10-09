package de.rockware.pma.connector.transformation.transformers;

import de.rockware.pma.connector.execution.beans.ExecutionContext;

public interface Transformer {

  void transform(ExecutionContext executionContext);
}
