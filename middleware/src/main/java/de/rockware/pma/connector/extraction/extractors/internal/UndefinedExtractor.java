package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndefinedExtractor implements Extractor {

  @Override
  public void extract(ExecutionContext executionContext) {
    log.debug("Nothing to do");
  }
}
