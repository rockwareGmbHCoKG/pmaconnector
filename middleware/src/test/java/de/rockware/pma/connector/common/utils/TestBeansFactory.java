package de.rockware.pma.connector.common.utils;

import de.rockware.pma.connector.common.factories.DefaultsFactory;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TestBeansFactory {

  public static ExecutionContext createExecutionContext() {
    return new ExecutionContext(createMappings(), createConfigurationValues(), createStartTime());
  }

  public static List<ConfigurationValue> createConfigurationValues() {
    return DefaultsFactory.createConfigurationValues();
  }

  public static List<Mapping> createMappings() {
    return DefaultsFactory.createMappings();
  }

  public static Date createStartTime() {
    return Date.from(
        LocalDateTime.of(2021, 9, 1, 12, 13, 44, 994235666)
            .atZone(ZoneId.systemDefault())
            .toInstant());
  }
}
