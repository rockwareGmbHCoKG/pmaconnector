package de.rockware.pma.reststub.utils;

import de.rockware.pma.connector.common.beans.JwtTokenHolder;
import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.converters.BeanConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import org.apache.commons.lang.RandomStringUtils;

public class TokenFactory {

  private static final String ALGORITHM_AND_TOKEN_TYPE = "{\"typ\":\"JWT\",\"alg\":\"HS512\"}";

  public static String get() {
    try {
      Token token =
          new Token(
              LocalDateTime.now()
                  .atZone(ZoneId.systemDefault())
                  .plusMinutes(30)
                  .toInstant()
                  .toEpochMilli());
      JwtTokenHolder encodedTokenHolder = new JwtTokenHolder();
      Base64.Encoder encoder = Base64.getEncoder();
      encodedTokenHolder.setJwtToken(
          String.format(
              "%s.%s.%s",
              encoder.encodeToString(ALGORITHM_AND_TOKEN_TYPE.getBytes()),
              encoder.encodeToString(BeanConverter.serialize(token).getBytes()),
              RandomStringUtils.randomAlphanumeric(86)));
      return BeanConverter.serialize(encodedTokenHolder);
    } catch (Throwable e) {
      throw new RuntimeException(String.format("Error creating token: %s", e.getMessage()), e);
    }
  }
}
