package de.rockware.pma.connector.common.retrievers;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static org.junit.jupiter.api.Assertions.*;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ConfigurationValueRetrieverTest {

  @Test
  void getNullParameters() {
    assertNull(ConfigurationValueRetriever.get(null, null));
  }

  @Test
  void getNullConfigurationValues() {
    assertNull(ConfigurationValueRetriever.get(null, ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getEmptyConfigurationValues() {
    assertNull(
        ConfigurationValueRetriever.get(Collections.emptyList(), ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getNullKey() {
    assertNull(ConfigurationValueRetriever.get(createConfigurationValues(), null));
  }

  @Test
  void getKeyNotFound() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.remove(0);
    assertNull(
        ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.MAIL_USERNAME));
  }

  @Test
  void getSuccess() {
    assertEquals(
        "INTERNAL",
        ConfigurationValueRetriever.get(
            createConfigurationValues(), ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getAsIntNullParameters() {
    assertEquals(0, ConfigurationValueRetriever.getAsInt(null, null));
  }

  @Test
  void getAsIntNullConfigurationValues() {
    assertEquals(0, ConfigurationValueRetriever.getAsInt(null, ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getAsIntEmptyConfigurationValues() {
    assertEquals(
        0,
        ConfigurationValueRetriever.getAsInt(
            Collections.emptyList(), ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getAsIntNullKey() {
    assertEquals(0, ConfigurationValueRetriever.getAsInt(createConfigurationValues(), null));
  }

  @Test
  void getAsIntKeyNotFound() {
    List<ConfigurationValue> configurationValues = createConfigurationValues();
    configurationValues.remove(0);
    assertEquals(
        0,
        ConfigurationValueRetriever.getAsInt(
            configurationValues, ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getAsIntNotANumber() {
    assertEquals(
        0,
        ConfigurationValueRetriever.getAsInt(
            createConfigurationValues(), ConfigurationKey.EXTRACTION_TYPE));
  }

  @Test
  void getAsIntSuccess() {
    assertEquals(
        10000,
        ConfigurationValueRetriever.getAsInt(
            createConfigurationValues(), ConfigurationKey.EXTRACTION_SFTP_SESSION_TIMEOUT));
  }

  @Test
  void getAsBooleanNullValue() {
    List<ConfigurationValue> configurationValues =
        createConfigurationValues().stream()
            .filter(v -> !v.getKey().equals(ConfigurationKey.MAIL_SENDING_ENABLED))
            .collect(Collectors.toList());
    assertFalse(
        ConfigurationValueRetriever.getAsBoolean(
            configurationValues, ConfigurationKey.MAIL_SENDING_ENABLED));
  }

  @Test
  void getAsBooleanEmptyValue() {
    List<ConfigurationValue> configurationValues =
        createConfigurationValues().stream()
            .map(
                v ->
                    new ConfigurationValue(
                        v.getKey(),
                        ConfigurationKey.MAIL_SENDING_ENABLED.equals(v.getKey())
                            ? ""
                            : v.getValue()))
            .collect(Collectors.toList());
    assertFalse(
        ConfigurationValueRetriever.getAsBoolean(
            configurationValues, ConfigurationKey.MAIL_SENDING_ENABLED));
  }

  @Test
  void getAsBooleanNotBooleanValue() {
    assertFalse(
        ConfigurationValueRetriever.getAsBoolean(
            createConfigurationValues(), ConfigurationKey.MAIL_USERNAME));
  }

  @Test
  void getAsBooleanSuccess() {
    assertTrue(
        ConfigurationValueRetriever.getAsBoolean(
            createConfigurationValues(), ConfigurationKey.DELETE_IMPORTED_CONFIGURATION_FILE));
  }
}
