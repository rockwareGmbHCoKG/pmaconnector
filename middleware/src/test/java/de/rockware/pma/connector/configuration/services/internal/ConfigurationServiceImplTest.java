package de.rockware.pma.connector.configuration.services.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.converters.StringConverter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.repositories.ConfigurationRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {
  @Mock private ConfigurationRepository configurationRepository;
  private ConfigurationServiceImpl sut;

  @BeforeEach
  void setUp() {
    this.sut = new ConfigurationServiceImpl(configurationRepository);
  }

  @Test
  void getAll() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    when(configurationRepository.findAll()).thenReturn(configurationValues);
    assertEquals(configurationValues, sut.getAll());
    verify(configurationRepository).findAll();
  }

  @Test
  void getValueNotFound() {
    assertNull(sut.getValue(ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getValueSuccess() {
    when(configurationRepository.findById(ConfigurationKey.EXTRACTION_TYPE))
        .thenReturn(
            Optional.of(
                new ConfigurationValue(
                    ConfigurationKey.EXTRACTION_TYPE,
                    ConfigurationKey.EXTRACTION_TYPE.getDefaultValue())));
    assertEquals(
        ConfigurationKey.EXTRACTION_TYPE.getDefaultValue(),
        sut.getValue(ConfigurationKey.EXTRACTION_TYPE));
    verify(configurationRepository).findById(eq(ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getValueAsIntSuccess() {
    when(configurationRepository.findById(ConfigurationKey.MAIL_SMTP_TIMEOUT))
        .thenReturn(
            Optional.of(
                new ConfigurationValue(
                    ConfigurationKey.MAIL_SMTP_TIMEOUT,
                    ConfigurationKey.MAIL_SMTP_TIMEOUT.getDefaultValue())));
    assertEquals(
        StringConverter.toInt(ConfigurationKey.MAIL_SMTP_TIMEOUT.getDefaultValue()),
        sut.getValueAsInt(ConfigurationKey.MAIL_SMTP_TIMEOUT));
    verify(configurationRepository).findById(eq(ConfigurationKey.MAIL_SMTP_TIMEOUT));
  }

  @Test
  void getValueAsBooleanSuccess() {
    when(configurationRepository.findById(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(
            Optional.of(
                new ConfigurationValue(
                    ConfigurationKey.MAIL_SENDING_ENABLED,
                    ConfigurationKey.MAIL_SENDING_ENABLED.getDefaultValue())));
    assertEquals(
        StringConverter.toBoolean(ConfigurationKey.MAIL_SENDING_ENABLED.getDefaultValue()),
        sut.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED));
    verify(configurationRepository).findById(eq(ConfigurationKey.MAIL_SENDING_ENABLED));
  }

  @Test
  void setAllNoValues() {
    sut.setAll();
    verify(configurationRepository).deleteAll();
    verifyNoMoreInteractions(configurationRepository);
  }

  @Test
  void setAllMultipleValues() {
    ConfigurationValue first =
        new ConfigurationValue(
            ConfigurationKey.EXTRACTION_TYPE, ConfigurationKey.EXTRACTION_TYPE.getDefaultValue());
    ConfigurationValue second =
        new ConfigurationValue(
            ConfigurationKey.TRANSFORMATION_TYPE,
            ConfigurationKey.TRANSFORMATION_TYPE.getDefaultValue());
    ConfigurationValue third =
        new ConfigurationValue(
            ConfigurationKey.LOAD_TYPE, ConfigurationKey.LOAD_TYPE.getDefaultValue());
    sut.setAll(first, second, third);
    verify(configurationRepository).deleteAll();
    verify(configurationRepository).saveAll(eq(Arrays.asList(first, second, third)));
  }

  @Test
  void clearSuccess() {
    sut.clear();
    verify(configurationRepository).deleteAll();
  }
}
