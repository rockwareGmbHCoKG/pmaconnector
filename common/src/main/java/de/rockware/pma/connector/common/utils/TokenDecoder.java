package de.rockware.pma.connector.common.utils;

import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.converters.BeanConverter;
import java.util.Base64;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

public class TokenDecoder {

  public static Token decode(String jwtToken) {
    try {
      if (StringUtils.isEmpty(jwtToken)) {
        return null;
      }
      String[] tokenParts = jwtToken.split("\\.");
      if (tokenParts.length < 3) {
        return null;
      }
      byte[] decoded = Base64.getDecoder().decode(tokenParts[1]);
      if (Objects.isNull(decoded) || decoded.length == 0) {
        return null;
      }
      Token token = BeanConverter.deserialize(new String(decoded), Token.class);
      if (Objects.isNull(token)) {
        return null;
      }
      return token;
    } catch (Throwable e) {
      return null;
    }
  }
}
