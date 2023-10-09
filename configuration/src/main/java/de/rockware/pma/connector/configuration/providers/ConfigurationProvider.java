package de.rockware.pma.connector.configuration.providers;

import de.rockware.pma.connector.configuration.beans.KeyValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.util.Collection;
import java.util.Map;

public interface ConfigurationProvider {

  Collection<KeyValue> create(Map<ConfigurationKey, KeyValue> defaultConfiguration);

  default void override(
      Map<ConfigurationKey, KeyValue> defaultConfiguretion, ConfigurationKey key, String value) {
    defaultConfiguretion.put(key, new KeyValue(key, value));
  }
}
