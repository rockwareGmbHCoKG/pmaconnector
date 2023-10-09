package de.rockware.pma.connector.common.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ValidationResult {
  private boolean valid;
  private String message;

  public ValidationResult() {
    this(true, null);
  }

  public ValidationResult(String message) {
    this(false, message);
  }

  public ValidationResult(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }
}
