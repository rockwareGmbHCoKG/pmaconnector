package de.rockware.pma.connector.execution.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Value {
  private String field;
  private String value;

  public Value() {}

  public Value(String field, String value) {
    this.field = field;
    this.value = value;
  }
}
