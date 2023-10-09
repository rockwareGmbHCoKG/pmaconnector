package de.rockware.pma.connector.common.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StatusHolder implements Serializable {
  private String status;

  public StatusHolder() {
    // for Jackson purposes
  }

  public StatusHolder(String status) {
    this.status = status;
  }
}
