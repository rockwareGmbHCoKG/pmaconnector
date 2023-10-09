package de.rockware.pma.connector.configuration.entities;

import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
public class ConfigurationValue implements Serializable {

  @Id private ConfigurationKey key;
  private String value;

  public ConfigurationValue() {}

  public ConfigurationValue(ConfigurationKey key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ConfigurationValue that = (ConfigurationValue) o;
    return Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
