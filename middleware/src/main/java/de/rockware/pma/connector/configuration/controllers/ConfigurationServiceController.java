package de.rockware.pma.connector.configuration.controllers;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.beans.AddValuesRequestBody;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/configuration")
@CrossOrigin(origins = "*")
@Slf4j
public class ConfigurationServiceController {
  private final ConfigurationService configurationService;

  @Autowired
  public ConfigurationServiceController(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @GetMapping("getAll")
  public Collection<ConfigurationValue> getAll() {
    return configurationService.getAll();
  }

  @PostMapping("setAll")
  public void setAll(@RequestBody AddValuesRequestBody<ConfigurationValue> body) {
    if (Objects.isNull(body)) {
      log.debug("Null body: skipped");
      return;
    }
    configurationService.setAll(body.getValues());
  }

  @PostMapping("clear")
  public void clear() {
    configurationService.clear();
  }
}
