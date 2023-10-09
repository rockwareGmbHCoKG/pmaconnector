package de.rockware.pma.connector.security.services.internal;

import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.utils.UrlBuilder;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.security.services.PmaRedirectUrlService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PmaRedirectUrlServiceImpl implements PmaRedirectUrlService {
  private static final long JWT_TOKEN_DURATION_MILLIS = 2 * 60 * 1000;

  private final ConfigurationService configurationService;

  @Autowired
  public PmaRedirectUrlServiceImpl(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Override
  public String getRedirectUrl() {
    Collection<ConfigurationValue> configurationValues = configurationService.getAll();
    String baseUrl =
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_REDIRECT_FRONTEND_BASE_URL);
    if (StringUtils.isEmpty(baseUrl)) {
      return "";
    }
    return UrlBuilder.append(
        baseUrl,
        Collections.singletonList(
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.PMA_REDIRECT_FRONTEND_FRAGMENT)),
        Collections.singletonList(createJwtTokenParameter(configurationValues)));
  }

  private String createJwtTokenParameter(Collection<ConfigurationValue> configurationValues) {
    String parameterName =
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_REDIRECT_FRONTEND_SECRET_PARAMETER_NAME);
    if (StringUtils.isEmpty(parameterName)) {
      return "";
    }
    String jwtToken = createJwtToken(configurationValues);
    return String.format("%s=%s", parameterName, jwtToken);
  }

  private String createJwtToken(Collection<ConfigurationValue> configurationValues) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(
        "firstname",
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_REDIRECT_FIRSTNAME));
    claims.put(
        "lastname",
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_REDIRECT_LASTNAME));
    claims.put(
        "email",
        ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.PMA_REDIRECT_EMAIL));
    claims.put(
        "username",
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_REDIRECT_USERNAME));
    claims.put(
        "masClientId",
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_CUSTOMER_ID));
    claims.put(
        "masId",
        ConfigurationValueRetriever.getAsInt(
            configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_ID));
    claims.put(
        "iss",
        ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.PMA_REDIRECT_ISS));
    return Jwts.builder()
        .addClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + JWT_TOKEN_DURATION_MILLIS))
        .signWith(
            SignatureAlgorithm.HS512,
            Base64.getEncoder()
                .encodeToString(
                    Optional.ofNullable(
                            ConfigurationValueRetriever.get(
                                configurationValues, ConfigurationKey.PMA_REDIRECT_SECRET))
                        .orElse("")
                        .getBytes()))
        .compact();
  }
}
