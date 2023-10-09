package de.rockware.pma.connector.mapping.enumerations;

import de.rockware.pma.connector.mapping.providers.MappingsProvider;
import de.rockware.pma.connector.mapping.providers.internal.LocalMappingsProvider;
import de.rockware.pma.connector.mapping.providers.internal.PmaMappingsProvider;
import java.util.Arrays;
import java.util.function.Supplier;

public enum MappingsType {
  PMA(PmaMappingsProvider::new),
  LOCAL(LocalMappingsProvider::new);

  private final Supplier<MappingsProvider> mappingsProviderFactory;

  MappingsType(Supplier<MappingsProvider> mappingsProviderFactory) {
    this.mappingsProviderFactory = mappingsProviderFactory;
  }

  public Supplier<MappingsProvider> getMappingsProviderFactory() {
    return mappingsProviderFactory;
  }

  public static MappingsType getByValue(String value) {
    return Arrays.stream(MappingsType.values())
        .filter(c -> c.name().equals(value))
        .findFirst()
        .orElse(null);
  }
}
