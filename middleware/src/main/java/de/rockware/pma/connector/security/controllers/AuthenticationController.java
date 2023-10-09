package de.rockware.pma.connector.security.controllers;

import de.rockware.pma.connector.common.beans.UrlHolder;
import de.rockware.pma.connector.security.services.PmaRedirectUrlService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("security")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthenticationController {

  @Autowired private PmaRedirectUrlService pmaRedirectUrlServiceImpl;

  @PostMapping("authenticate")
  public ResponseEntity<String> authenticate() {
    return new ResponseEntity<>("Authentication granted", HttpStatus.OK);
  }

  @GetMapping("redirect")
  public ResponseEntity<Object> redirect() throws Exception {
    URI url = new URI(pmaRedirectUrlServiceImpl.getRedirectUrl());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(url);
    return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
  }

  @GetMapping("redirectUrl")
  public UrlHolder redirectUrl() {
    return new UrlHolder(pmaRedirectUrlServiceImpl.getRedirectUrl());
  }
}
