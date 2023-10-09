package de.rockware.pma.connector.security.providers;

import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import java.util.ArrayList;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class PmaConnectorAuthenticationProvider implements AuthenticationProvider {

  private final ConfigurationService configurationService;

  public PmaConnectorAuthenticationProvider(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password = authentication.getCredentials().toString();
    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
      return null;
    }
    String restAuthUsername = configurationService.getValue(ConfigurationKey.REST_AUTH_USERNAME);
    String restAuthPassword = configurationService.getValue(ConfigurationKey.REST_AUTH_PASSWORD);
    if (name.equals(restAuthUsername) && password.equals(restAuthPassword)) {
      return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
    } else {
      return null;
    }
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return Objects.nonNull(aClass) && aClass.equals(UsernamePasswordAuthenticationToken.class);
  }
}
