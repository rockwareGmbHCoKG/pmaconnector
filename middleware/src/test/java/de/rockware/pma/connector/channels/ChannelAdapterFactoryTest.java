package de.rockware.pma.connector.channels;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rockware.pma.connector.channels.internal.FallbackChannelAdapter;
import de.rockware.pma.connector.channels.internal.SftpChannelAdapter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChannelAdapterFactoryTest {
  private ChannelAdapterFactory sut;

  @BeforeEach
  void setUp() {
    this.sut = new ChannelAdapterFactory();
  }

  @Test
  void nullConfigurationValues() {
    ChannelAdapter channelAdapter = sut.create(null);
    assertEquals(
        FallbackChannelAdapter.class.getSimpleName(), channelAdapter.getClass().getSimpleName());
  }

  @Test
  void noSourceType() {
    ChannelAdapter channelAdapter = sut.create(Collections.emptyList());
    assertEquals(
        FallbackChannelAdapter.class.getSimpleName(), channelAdapter.getClass().getSimpleName());
  }

  @Test
  void notExistingSourceType() {
    ChannelAdapter channelAdapter =
        sut.create(
            createConfigurationValues().stream()
                .filter(v -> !v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
                .collect(Collectors.toList()));
    assertEquals(
        FallbackChannelAdapter.class.getSimpleName(), channelAdapter.getClass().getSimpleName());
  }

  @Test
  void sftp() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.SFTP.name()));
    ChannelAdapter channelAdapter = sut.create(configurationValues);
    assertEquals(
        SftpChannelAdapter.class.getSimpleName(), channelAdapter.getClass().getSimpleName());
  }

  @Test
  void fallback() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.stream()
        .filter(v -> v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.UNDEFINED.name()));
    ChannelAdapter channelAdapter = sut.create(configurationValues);
    assertEquals(
        FallbackChannelAdapter.class.getSimpleName(), channelAdapter.getClass().getSimpleName());
  }
}
