package de.rockware.pma.connector.common.factories;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultsFactory {

  public static List<ConfigurationValue> createConfigurationValues() {
    List<ConfigurationValue> configurationValues = new ArrayList<>();
    Arrays.stream(ConfigurationKey.values())
        .forEach(v -> configurationValues.add(new ConfigurationValue(v, v.getDefaultValue())));
    return configurationValues;
  }

  public static List<Mapping> createMappings() {
    return IntStream.rangeClosed(1, 5)
        .mapToObj(
            i -> new Mapping(i, String.format("Field %d", i), String.format("Field %d M", i), i))
        .collect(Collectors.toList());
  }
}
