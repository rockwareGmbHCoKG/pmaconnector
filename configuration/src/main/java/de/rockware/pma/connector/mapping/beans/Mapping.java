package de.rockware.pma.connector.mapping.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Mapping implements Serializable {
  private long id;
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
}
