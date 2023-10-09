package de.rockware.pma.connector.common.retrievers;

import de.rockware.pma.connector.common.converters.StringConverter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class ConfigurationValueRetriever {

  public static String get(
      Collection<ConfigurationValue> configurationValues, ConfigurationKey key) {
    if (Objects.isNull(configurationValues)
        || configurationValues.isEmpty()
        || Objects.isNull(key)) {
      log.debug(
          String.format(
              "Invalid parameters: configurationValues=%s, key=%s, returning null",
              configurationValues, key));
      return null;
    }
    String value =
        configurationValues.stream()
            .filter(v -> key.equals(v.getKey()))
            .findFirst()
            .map(ConfigurationValue::getValue)
            .orElse(null);
    if (StringUtils.isEmpty(value)) {
      log.debug(String.format("Value not found for key %s", key));
    }
    return value;
  }

  public static int getAsInt(
      Collection<ConfigurationValue> configurationValues, ConfigurationKey key) {
    String value = "undefined";
    try {
      return StringConverter.toInt(get(configurationValues, key));
    } catch (Throwable e) {
      log.error(String.format("Error parsing %s as int", value));
      return 0;
    }
  }

  public static boolean getAsBoolean(
      Collection<ConfigurationValue> configurationValues, ConfigurationKey key) {
    return StringConverter.toBoolean(get(configurationValues, key));
  }
}
