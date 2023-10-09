package de.rockware.pma.connector.configuration.services;

import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;

public interface ConfigurationService extends ReadWriteService<ConfigurationValue> {

  String getValue(ConfigurationKey configurationKey);

  int getValueAsInt(ConfigurationKey configurationKey);

  boolean getValueAsBoolean(ConfigurationKey configurationKey);
}
