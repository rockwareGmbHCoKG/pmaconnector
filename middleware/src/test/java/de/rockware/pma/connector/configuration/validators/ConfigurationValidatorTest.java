package de.rockware.pma.connector.configuration.validators;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static org.junit.jupiter.api.Assertions.*;

import de.rockware.pma.connector.configuration.beans.ConfigurationValidationResult;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ConfigurationValidatorTest {

  @Test
  void nullConfigurationValues() {
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(null);
    assertNotNull(configurationValidationResult);
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void emptyConfigurationValues() {
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(Collections.emptyList());
    assertNotNull(configurationValidationResult);
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void mailSendingDisabled() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(ConfigurationKey.MAIL_SENDING_ENABLED);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertNotNull(configurationValidationResult);
    assertEquals(
        "MAIL: MAIL_SENDING_ENABLED - Empty boolean value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void invalidMail() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(
            ConfigurationKey.MAIL_SENDER, ConfigurationKey.MAIL_SMTP_SOCKET_FACTORY_CLASS);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "MAIL: MAIL_SENDER - Empty string value\nMAIL: MAIL_SMTP_SOCKET_FACTORY_CLASS - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void mailWithoutRecipients() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(
            ConfigurationKey.MAIL_TO_RECIPIENTS,
            ConfigurationKey.MAIL_CC_RECIPIENTS,
            ConfigurationKey.MAIL_BCC_RECIPIENTS);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "MAIL: neither MAIL_TO_RECIPIENTS nor MAIL_CC_RECIPIENTS nor MAIL_BCC_RECIPIENTS defined",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void invalidExtraction() {
    List<ConfigurationValue> configurationValues = getWithoutKeys(ConfigurationKey.EXTRACTION_TYPE);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "EXTRACTION: EXTRACTION_TYPE - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void noExtractionSftpKeysButDifferentExtractionType() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(
            ConfigurationKey.EXTRACTION_SFTP_HOST, ConfigurationKey.EXTRACTION_SFTP_PATH);
    configurationValues.forEach(
        v -> {
          if (!v.getKey().equals(ConfigurationKey.EXTRACTION_TYPE)) {
            return;
          }
          v.setValue(ExtractionType.TEST.name());
        });
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertTrue(configurationValidationResult.isValid());
  }

  @Test
  void invalidExtractionSftp() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(ConfigurationKey.EXTRACTION_SFTP_PATH);
    configurationValues.stream()
        .filter(v -> ConfigurationKey.EXTRACTION_TYPE.equals(v.getKey()))
        .forEach(v -> v.setValue(ExtractionType.SFTP.name()));
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "EXTRACTION_SFTP: EXTRACTION_SFTP_PATH - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void invalidExtractionFtp() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(ConfigurationKey.EXTRACTION_FTP_PATH);
    configurationValues.stream()
        .filter(v -> ConfigurationKey.EXTRACTION_TYPE.equals(v.getKey()))
        .findFirst()
        .ifPresent(v -> v.setValue(ExtractionType.FTP.name()));
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "EXTRACTION_FTP: EXTRACTION_FTP_PATH - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void invalidTransformation() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(ConfigurationKey.TRANSFORMATION_TYPE);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "TRANSFORMATION: TRANSFORMATION_TYPE - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void invalidLoad() {
    List<ConfigurationValue> configurationValues = getWithoutKeys(ConfigurationKey.LOAD_TYPE);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "LOAD: LOAD_TYPE - Empty string value", configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  @Test
  void noLoadPmaKeysButDifferentLoadType() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(
            ConfigurationKey.PMA_AUTHENTICATION_SECRET,
            ConfigurationKey.PMA_PARTNER_SYSTEM_CUSTOMER_ID);
    configurationValues.forEach(
        v -> {
          if (!v.getKey().equals(ConfigurationKey.LOAD_TYPE)) {
            return;
          }
          v.setValue(ExtractionType.UNDEFINED.name());
        });
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertTrue(configurationValidationResult.isValid());
  }

  @Test
  void invalidLoadPma() {
    List<ConfigurationValue> configurationValues =
        getWithoutKeys(ConfigurationKey.PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT);
    ConfigurationValidationResult configurationValidationResult =
        ConfigurationValidator.validate(configurationValues);
    assertEquals(
        "LOAD_PMA_REST: PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT - Empty string value",
        configurationValidationResult.getMessage());
    assertFalse(configurationValidationResult.isValid());
  }

  private List<ConfigurationValue> getWithoutKeys(ConfigurationKey... keys) {
    return createConfigurationValues().stream()
        .filter(v -> Arrays.stream(keys).noneMatch(k -> k.equals(v.getKey())))
        .collect(Collectors.toList());
  }
}
