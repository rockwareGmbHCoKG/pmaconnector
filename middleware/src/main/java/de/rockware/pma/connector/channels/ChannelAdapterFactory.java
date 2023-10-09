package de.rockware.pma.connector.channels;

import de.rockware.pma.connector.channels.internal.FallbackChannelAdapter;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import java.util.Collection;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ChannelAdapterFactory
    implements Factory<ChannelAdapter, Collection<ConfigurationValue>> {

  @Override
  public ChannelAdapter create(Collection<ConfigurationValue> configurationValues) {
    FallbackChannelAdapter fallbackChannelAdapter = new FallbackChannelAdapter();
    if (Objects.isNull(configurationValues)) {
      return fallbackChannelAdapter;
    }
    ExtractionType extractionType =
        ExtractionType.getByValue(
            ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.EXTRACTION_TYPE));
    return extractionType.getChannelAdapterFactory().get();
  }
}
