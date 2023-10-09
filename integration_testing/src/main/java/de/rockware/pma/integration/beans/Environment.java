package de.rockware.pma.integration.beans;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Environment implements Serializable {
  private String hostIp;
  private String pmaConnectorPort;
  private String restStubPort;
  private String username;
  private String password;

  public String getEncodedUserAndPassword() {
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      return null;
    }
    return Base64.getEncoder()
        .encodeToString(
            String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8));
  }
}
