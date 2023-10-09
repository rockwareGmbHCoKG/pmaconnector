package de.rockware.pma.connector.extraction;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.factories.StepFactory;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.extraction.extractors.internal.CompositeExtractor;
import de.rockware.pma.connector.extraction.extractors.internal.ExecutionStatusUpdaterExtractor;
import de.rockware.pma.connector.extraction.extractors.internal.InfoUpdaterExtractor;
import de.rockware.pma.connector.extraction.extractors.internal.UndefinedExtractor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtractorFactory implements Factory<Extractor, ExecutionContext> {
  private final ExecutionStatusService executionStatusService;

  @Autowired
  public ExtractorFactory(ExecutionStatusService executionStatusService) {
    this.executionStatusService = executionStatusService;
  }

  @Override
  public Extractor create(ExecutionContext executionContext) {
    return StepFactory.create(
        Step.EXTRACTION,
        executionContext,
        ConfigurationKey.EXTRACTION_TYPE,
        v -> createExtractor(executionContext, v),
        new UndefinedExtractor());
  }

  private Extractor createExtractor(ExecutionContext executionContext, String value) {
    ExtractionType extractionType = ExtractionType.getByValue(value);
    List<Extractor> extractors = new ArrayList<>();
    extractors.add(extractionType.getExtractorFactory().apply(executionContext));
    if (extractionType.isUpdateInfo()) {
      extractors.add(new InfoUpdaterExtractor(executionStatusService));
    }
    extractors.add(new ExecutionStatusUpdaterExtractor(executionStatusService));
    return new CompositeExtractor(extractors);
  }
}
