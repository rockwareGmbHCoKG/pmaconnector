package de.rockware.pma.connector.transformation;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.TransformationType;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import de.rockware.pma.connector.transformation.transformers.internal.MappingTransformer;
import de.rockware.pma.connector.transformation.transformers.internal.UndefinedTransformer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransformerFactoryTest {
  private TransformerFactory sut;

  @BeforeEach
  void setUp() {
    this.sut = new TransformerFactory();
  }

  @Test
  void nullConfigurationValues() {
    Transformer transformer = sut.create(null);
    assertEquals(
        UndefinedTransformer.class.getSimpleName(), transformer.getClass().getSimpleName());
  }

  @Test
  void noSourceType() {
    Transformer transformer =
        sut.create(
            new ExecutionContext(createMappings(), Collections.emptyList(), createStartTime()));
    assertEquals(
        UndefinedTransformer.class.getSimpleName(), transformer.getClass().getSimpleName());
  }

  @Test
  void notExistingTransformationType() {
    Transformer transformer =
        sut.create(
            new ExecutionContext(
                createMappings(),
                createConfigurationValues().stream()
                    .filter(v -> !v.getKey().equals(ConfigurationKey.TRANSFORMATION_TYPE))
                    .collect(Collectors.toList()),
                createStartTime()));
    assertEquals(
        UndefinedTransformer.class.getSimpleName(), transformer.getClass().getSimpleName());
  }

  @Test
  void mapping() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.TRANSFORMATION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(TransformationType.MAPPING.name()));
    Transformer transformer =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(MappingTransformer.class.getSimpleName(), transformer.getClass().getSimpleName());
  }

  @Test
  void undefined() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.TRANSFORMATION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(TransformationType.UNDEFINED.name()));
    Transformer transformer =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(
        UndefinedTransformer.class.getSimpleName(), transformer.getClass().getSimpleName());
  }
}
