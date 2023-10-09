package de.rockware.pma.reststub.controllers;

import de.rockware.pma.connector.common.beans.AuthenticationBody;
import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.reststub.common.Constants;
import de.rockware.pma.reststub.utils.TokenFactory;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/authentication/partnersystem")
@CrossOrigin(origins = "*")
public class AuthenticationController {

  @PostMapping(value = "credentialsbased", produces = "application/json")
  public ResponseEntity<String> credentialsBased(@RequestBody String body) {
    try {
      if (StringUtils.isEmpty(body)) {
        throw new RuntimeException("Empty body");
      }
      AuthenticationBody authenticationBody =
          BeanConverter.deserialize(body, AuthenticationBody.class);
      if (Objects.isNull(authenticationBody)) {
        throw new RuntimeException("Null authentication body");
      }
      if (!Constants.AUTHORIZED_PARTNERSYSTEM_ID.equals(authenticationBody.getPartnerSystemIdExt())
          || !Constants.AUTHORIZED_PARTNERSYSTEM_CUSTOMER_ID.equals(
              authenticationBody.getPartnerSystemCustomerIdExt())
          || !Constants.AUTORIZED_AUTHENTICATION_SECRET.equals(
              authenticationBody.getAuthenticationSecret())) {
        throw new RuntimeException("Invalid credentials");
      }
      return new ResponseEntity<>(TokenFactory.get(), HttpStatus.OK);
    } catch (Throwable e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
