package de.rockware.pma.connector.configuration.enumerations;

import de.rockware.pma.connector.configuration.providers.ConfigurationProvider;
import de.rockware.pma.connector.configuration.providers.internal.*;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public enum ConfigurationType {
  LOCAL_TO_LOCAL(LocalToLocalConfigurationProvider::new),
  LOCAL_TO_PMA_TEST(LocalToPmaTestConfigurationProvider::new),
  LOCAL_TO_PMA_PROD(LocalToPmaProdConfigurationProvider::new);

  private final Supplier<ConfigurationProvider> configurationProviderFactory;

  ConfigurationType(Supplier<ConfigurationProvider> configurationProviderFactory) {
    this.configurationProviderFactory = configurationProviderFactory;
  }

  public static ConfigurationType getByValue(String value) {
    return Arrays.stream(ConfigurationType.values())
        .filter(c -> c.name().equals(value))
        .findFirst()
        .orElse(null);
  }
}
