package de.rockware.pma.reststub.utils;

import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.beans.ValidationResult;
import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.reststub.common.Constants;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

public class AuthTokenValidator {

  public static ValidationResult validate(Map<String, String> headers) {
    try {
      if (Objects.isNull(headers)) {
        return new ValidationResult("No headers");
      }
      String authHeader = headers.get("authorization");
      if (StringUtils.isEmpty(authHeader)) {
        return new ValidationResult("No authorization header");
      }
      String encodedCredentials = StringUtils.removeStart(authHeader, "Bearer ");
      if (StringUtils.isEmpty(encodedCredentials)) {
        return new ValidationResult("No encoded credentials");
      }
      String[] credentialsParts = encodedCredentials.split("\\.");
      if (credentialsParts.length < 3) {
        return new ValidationResult("Invalid JWT token");
      }
      byte[] decoded = Base64.getDecoder().decode(credentialsParts[1]);
      if (Objects.isNull(decoded) || decoded.length == 0) {
        return new ValidationResult("Error decoding credentials");
      }
      Token token = BeanConverter.deserialize(new String(decoded), Token.class);
      if (Objects.isNull(token)) {
        return new ValidationResult("Null decoded token");
      }
      if (Arrays.stream(token.getCustomerIds())
          .noneMatch(Constants.AUTHORIZED_CUSTOMER_ID::equals)) {
        return new ValidationResult("Invalid token");
      }
      return new ValidationResult();
    } catch (Throwable e) {
      return new ValidationResult(String.format("Error validating headers: %s", e.getMessage()));
    }
  }
}
