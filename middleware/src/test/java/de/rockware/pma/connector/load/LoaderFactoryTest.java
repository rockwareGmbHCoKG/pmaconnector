package de.rockware.pma.connector.load;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.LoadType;
import de.rockware.pma.connector.configuration.enumerations.TransformationType;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.internal.PmaRestLoader;
import de.rockware.pma.connector.load.loaders.internal.UndefinedLoader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoaderFactoryTest {

  @Mock private RequestExecutor requestExecutor;
  @Mock private ExecutionStatusService executionStatusService;
  @Mock private TransferredRecipientsService transferredRecipientsService;

  private LoaderFactory sut;

  @BeforeEach
  void setUp() {
    this.sut =
        new LoaderFactory(requestExecutor, executionStatusService, transferredRecipientsService);
  }

  @Test
  void nullConfigurationValues() {
    Loader loader = sut.create(null);
    assertEquals(UndefinedLoader.class.getSimpleName(), loader.getClass().getSimpleName());
  }

  @Test
  void noSourceType() {
    Loader loader =
        sut.create(
            new ExecutionContext(createMappings(), Collections.emptyList(), createStartTime()));
    assertEquals(UndefinedLoader.class.getSimpleName(), loader.getClass().getSimpleName());
  }

  @Test
  void notExistingTransformationType() {
    Loader loader =
        sut.create(
            new ExecutionContext(
                createMappings(),
                createConfigurationValues().stream()
                    .filter(v -> !v.getKey().equals(ConfigurationKey.LOAD_TYPE))
                    .collect(Collectors.toList()),
                createStartTime()));
    assertEquals(UndefinedLoader.class.getSimpleName(), loader.getClass().getSimpleName());
  }

  @Test
  void pmaRest() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.LOAD_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(LoadType.PMA_REST.name()));
    Loader loader =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(PmaRestLoader.class.getSimpleName(), loader.getClass().getSimpleName());
  }

  @Test
  void undefined() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.LOAD_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(TransformationType.UNDEFINED.name()));
    Loader loader =
        sut.create(new ExecutionContext(createMappings(), configurationValues, createStartTime()));
    assertEquals(UndefinedLoader.class.getSimpleName(), loader.getClass().getSimpleName());
  }
}
