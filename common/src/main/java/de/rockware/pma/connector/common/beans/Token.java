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
public class Token implements Serializable {
  private String sub;
  private String iss;
  private String fullName;
  private String[] customerIds;
  private Long exp;
  private String locale;
  private Long iat;
  private int userId;
  private String[] authorities;

  public Token() {
    // for Jackson purposes
  }

  public Token(long time) {
    this.sub = "pma.connector.id";
    this.iss = "rockware.de";
    this.fullName = "PMA Connector user";
    this.customerIds = new String[] {"42"};
    this.exp = time;
    this.locale = "en";
    this.iat = time;
    this.userId = 42;
    this.authorities = new String[] {"ROLE_PMP_ACCESS"};
  }
}
