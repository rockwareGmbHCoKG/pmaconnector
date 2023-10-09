package de.rockware.pma.connector.configuration.beans;

import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class KeyValue implements Serializable {
  private ConfigurationKey key;
  private String value;

  public KeyValue() {
    // for JPA purposes
  }

  public KeyValue(ConfigurationKey key, String value) {
    this.key = key;
    this.value = value;
  }
}
