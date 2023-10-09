package de.rockware.pma.connector.configuration.validators;

import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.beans.ConfigurationValidationResult;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey.ConfigurationGroup;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey.ConfigurationValueType;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import de.rockware.pma.connector.configuration.enumerations.LoadType;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class ConfigurationValidator {

  public static ConfigurationValidationResult validate(
      Collection<ConfigurationValue> configurationValues) {
    if (Objects.isNull(configurationValues)) {
      return new ConfigurationValidationResult("Null configuration values");
    }
    if (configurationValues.isEmpty()) {
      return new ConfigurationValidationResult("Empty configuration values");
    }
    List<String> errors = new ArrayList<>();
    Map<ConfigurationGroup, CustomValidator> customValidators = new HashMap<>();
    customValidators.put(
        ConfigurationGroup.MAIL, e -> validateMailRecipients(configurationValues, e));
    customValidators.put(
        ConfigurationGroup.EXTRACTION, e -> validateExtraction(configurationValues, e));
    customValidators.put(ConfigurationGroup.LOAD, e -> validateLoad(configurationValues, e));
    for (ConfigurationGroup value : ConfigurationGroup.values()) {
      validateKeys(errors, configurationValues, value, customValidators.get(value), false);
    }
    if (errors.isEmpty()) {
      return new ConfigurationValidationResult();
    }
    return new ConfigurationValidationResult(
        errors.stream().reduce((s1, s2) -> String.format("%s\n%s", s1, s2)).orElse(""));
  }

  private static void validateKeys(
      List<String> errors,
      Collection<ConfigurationValue> configurationValues,
      ConfigurationGroup group,
      CustomValidator customValidator,
      boolean ignoreOptional) {
    List<ConfigurationKey> keys =
        Arrays.stream(ConfigurationKey.values())
            .filter(k -> group.equals(k.getGroup()))
            .collect(Collectors.toList());
    for (ConfigurationKey key : keys) {
      if (!ignoreOptional && key.isOptional()) {
        continue;
      }
      String value = ConfigurationValueRetriever.get(configurationValues, key);
      if (ConfigurationValueType.INTEGER.equals(key.getValueType())) {
        validateInteger(key, value, group.name(), errors);
        continue;
      }
      if (ConfigurationValueType.BOOLEAN.equals(key.getValueType())) {
        validateBoolean(key, value, group.name(), errors);
        continue;
      }
      validateString(key, value, group.name(), errors);
    }
    if (Objects.nonNull(customValidator)) {
      customValidator.validate(errors);
    }
  }

  private static void validateInteger(
      ConfigurationKey key, String value, String context, List<String> errors) {
    if (StringUtils.isEmpty(value)) {
      errors.add(String.format("%s: %s - Empty integer value", context, key));
      return;
    }
    try {
      Integer.parseInt(value);
    } catch (Throwable e) {
      errors.add(String.format("%s: %s - Not an integer: %s", context, key, value));
    }
  }

  private static void validateBoolean(
      ConfigurationKey key, String value, String context, List<String> errors) {
    if (StringUtils.isEmpty(value)) {
      errors.add(String.format("%s: %s - Empty boolean value", context, key));
    }
  }

  private static void validateString(
      ConfigurationKey key, String value, String context, List<String> errors) {
    if (StringUtils.isEmpty(value)) {
      errors.add(String.format("%s: %s - Empty string value", context, key));
    }
  }

  private static void validateMailRecipients(
      Collection<ConfigurationValue> configurationValues, List<String> errors) {
    if (StringUtils.isNotEmpty(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.MAIL_TO_RECIPIENTS))) {
      return;
    }
    if (StringUtils.isNotEmpty(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.MAIL_CC_RECIPIENTS))) {
      return;
    }
    if (StringUtils.isNotEmpty(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.MAIL_BCC_RECIPIENTS))) {
      return;
    }
    errors.add(
        String.format(
            "%s: neither %s nor %s nor %s defined",
            ConfigurationGroup.MAIL.name(),
            ConfigurationKey.MAIL_TO_RECIPIENTS.name(),
            ConfigurationKey.MAIL_CC_RECIPIENTS.name(),
            ConfigurationKey.MAIL_BCC_RECIPIENTS.name()));
  }

  private static void validateExtraction(
      Collection<ConfigurationValue> configurationValues, List<String> errors) {
    if (ExtractionType.SFTP
        .name()
        .equals(
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.EXTRACTION_TYPE))) {
      validateKeys(errors, configurationValues, ConfigurationGroup.EXTRACTION_SFTP, null, true);
    }
    if (ExtractionType.FTP
        .name()
        .equals(
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.EXTRACTION_TYPE))) {
      validateKeys(errors, configurationValues, ConfigurationGroup.EXTRACTION_FTP, null, true);
    }
  }

  private static void validateLoad(
      Collection<ConfigurationValue> configurationValues, List<String> errors) {
    if (LoadType.PMA_REST
        .name()
        .equals(ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.LOAD_TYPE))) {
      validateKeys(errors, configurationValues, ConfigurationGroup.LOAD_PMA_REST, null, true);
    }
  }

  private interface CustomValidator {
    void validate(List<String> errors);
  }
}
