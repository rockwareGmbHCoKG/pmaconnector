package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.converters.BeanConverter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;

class TokenDecoderTest {
  private static final String ALGORITHM_AND_TOKEN_TYPE = "{\"typ\":\"JWT\",\"alg\":\"HS512\"}";

  @Test
  void nullValue() {
    assertNull(TokenDecoder.decode(null));
  }

  @Test
  void emptyValue() {
    assertNull(TokenDecoder.decode(""));
  }

  @Test
  void notBase64Encoded() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("sample_token.json"))
                        .toURI())));
    assertNull(TokenDecoder.decode(value));
  }

  @Test
  void emptyStringEncoded() {
    String encoded = Base64.getEncoder().encodeToString(new byte[0]);
    assertNull(TokenDecoder.decode(encoded));
  }

  @Test
  void success() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("sample_token.json"))
                        .toURI())));
    Token expected = new Token();
    expected.setSub("paul");
    expected.setIss("com.dpdhl.dialogmarketing");
    expected.setFullName("Paul Watson");
    expected.setCustomerIds(new String[] {"4"});
    expected.setExp(1588272706L);
    expected.setLocale("de");
    expected.setIat(1588269106L);
    expected.setUserId(2);
    expected.setAuthorities(new String[] {"ROLE_PMP_ACCESS"});
    Base64.Encoder encoder = Base64.getEncoder();
    String encodedToken =
        String.format(
            "%s.%s.%s",
            encoder.encodeToString(ALGORITHM_AND_TOKEN_TYPE.getBytes()),
            encoder.encodeToString(BeanConverter.serialize(expected).getBytes()),
            RandomStringUtils.randomAlphanumeric(86));
    assertEquals(expected, TokenDecoder.decode(encodedToken));
  }
}
