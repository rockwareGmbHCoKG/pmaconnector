package de.rockware.pma.reststub.controllers;

import de.rockware.pma.connector.common.beans.StatusHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("status")
@CrossOrigin(origins = "*")
public class StatusController {

  @GetMapping(value = "get", produces = "application/json")
  public StatusHolder get() {
    return new StatusHolder("UP");
  }
}
