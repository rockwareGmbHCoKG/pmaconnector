package de.rockware.pma.connector.common.factories;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class StepFactory {

  public static <T> T create(
      @NonNull Step step,
      ExecutionContext executionContext,
      ConfigurationKey key,
      Function<String, T> converter,
      @NonNull T fallback) {
    String stepName = step.name();
    if (Objects.isNull(executionContext)) {
      log.error(String.format("No execution context: returning fallback %s executor", stepName));
      return fallback;
    }
    Collection<ConfigurationValue> configurationValues = executionContext.getConfigurationValues();
    if (Objects.isNull(configurationValues) || configurationValues.isEmpty()) {
      log.error(String.format("No configuration values: returning fallback %s executor", stepName));
      return fallback;
    }
    T stepExecutor = converter.apply(ConfigurationValueRetriever.get(configurationValues, key));
    log.debug(String.format("%s step executor created: %s", stepName, stepExecutor));
    return stepExecutor;
  }
}
