package de.rockware.pma.connector.configuration.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationValidationResult {
  private boolean valid;
  private String message;

  public ConfigurationValidationResult() {
    this(true, null);
  }

  public ConfigurationValidationResult(String message) {
    this(false, message);
  }

  public ConfigurationValidationResult(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }
}
