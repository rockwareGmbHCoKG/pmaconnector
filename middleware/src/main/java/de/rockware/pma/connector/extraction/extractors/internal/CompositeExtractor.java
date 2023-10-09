package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import java.util.List;

public class CompositeExtractor implements Extractor {

  private final List<Extractor> extractors;

  public CompositeExtractor(List<Extractor> extractors) {
    this.extractors = extractors;
  }

  @Override
  public void extract(ExecutionContext executionContext) {
    for (Extractor extractor : extractors) {
      extractor.extract(executionContext);
    }
  }
}
