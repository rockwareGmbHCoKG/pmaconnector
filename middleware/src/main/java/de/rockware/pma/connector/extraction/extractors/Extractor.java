package de.rockware.pma.connector.extraction.extractors;

import de.rockware.pma.connector.execution.beans.ExecutionContext;

public interface Extractor {

  void extract(ExecutionContext executionContext);
}
