package de.rockware.pma.connector.extraction;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.extraction.extractors.internal.CompositeExtractor;
import de.rockware.pma.connector.extraction.extractors.internal.UndefinedExtractor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtractorFactoryTest {

  private ExtractorFactory sut;

  @Mock private ExecutionStatusService executionStatusService;

  @BeforeEach
  void setUp() {
    this.sut = new ExtractorFactory(executionStatusService);
  }

  @Test
  void nullConfigurationValues() {
    Extractor extractor = sut.create(null);
    assertEquals(UndefinedExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void noSourceType() {
    Extractor extractor =
        sut.create(new ExecutionContext(createMappings(), Collections.emptyList(), null));
    assertEquals(UndefinedExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void notExistingSourceType() {
    Extractor extractor =
        sut.create(
            new ExecutionContext(
                createMappings(),
                createConfigurationValues().stream()
                    .filter(v -> !v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
                    .collect(Collectors.toList()),
                createStartTime()));
    assertEquals(CompositeExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void sftp() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.SFTP.name()));
    Extractor extractor =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(CompositeExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void ftp() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.FTP.name()));
    Extractor extractor =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(CompositeExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void test() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.TEST.name()));
    Extractor extractor =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(CompositeExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }

  @Test
  void undefined() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.UNDEFINED.name()));
    Extractor extractor =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(CompositeExtractor.class.getSimpleName(), extractor.getClass().getSimpleName());
  }
}
