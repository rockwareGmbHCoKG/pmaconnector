package de.rockware.pma.connector.configuration.services.internal;

import de.rockware.pma.connector.common.converters.StringConverter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.repositories.ConfigurationRepository;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

  private final ConfigurationRepository configurationRepository;

  @Autowired
  public ConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  @Override
  public Collection<ConfigurationValue> getAll() {
    return configurationRepository.findAll();
  }

  @Override
  public String getValue(ConfigurationKey configurationKey) {
    return configurationRepository
        .findById(configurationKey)
        .map(ConfigurationValue::getValue)
        .orElse(null);
  }

  @Override
  public int getValueAsInt(ConfigurationKey configurationKey) {
    return StringConverter.toInt(getValue(configurationKey));
  }

  @Override
  public boolean getValueAsBoolean(ConfigurationKey configurationKey) {
    return StringConverter.toBoolean(getValue(configurationKey));
  }

  @Override
  public void setAll(ConfigurationValue... configurationValues) {
    clear();
    if (Objects.isNull(configurationValues) || configurationValues.length == 0) {
      log.debug("No configurationValues to be added");
      return;
    }
    configurationRepository.saveAll(Arrays.asList(configurationValues));
  }

  @Override
  public void clear() {
    configurationRepository.deleteAll();
  }
}
