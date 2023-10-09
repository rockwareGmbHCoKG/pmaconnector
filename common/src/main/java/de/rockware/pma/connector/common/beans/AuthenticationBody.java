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
public class AuthenticationBody implements Serializable {
  private String partnerSystemIdExt;
  private String partnerSystemCustomerIdExt;
  private String authenticationSecret;
  private String locale;

  public AuthenticationBody() {
    // for Jackson purposes
  }

  public AuthenticationBody(
      String partnerSystemIdExt,
      String partnerSystemCustomerIdExt,
      String authenticationSecret,
      String locale) {
    this.partnerSystemIdExt = partnerSystemIdExt;
    this.partnerSystemCustomerIdExt = partnerSystemCustomerIdExt;
    this.authenticationSecret = authenticationSecret;
    this.locale = locale;
  }
}
