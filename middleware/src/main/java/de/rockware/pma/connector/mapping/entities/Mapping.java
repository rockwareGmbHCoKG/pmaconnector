package de.rockware.pma.connector.mapping.entities;

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
public class Mapping implements Serializable {
  @Id private long id;
  private String source;
  private String target;
  private int type;

  public Mapping() {}

  public Mapping(long id, String source, String target, int type) {
    this.id = id;
    this.source = source;
    this.target = target;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Mapping mapping = (Mapping) o;
    return Objects.equals(id, mapping.id);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
