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
public class UrlHolder implements Serializable {
  private String url;

  public UrlHolder() {
    // for Jackson purposes
  }

  public UrlHolder(String url) {
    this.url = url;
  }
}
