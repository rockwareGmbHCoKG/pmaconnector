package de.rockware.pma.connector.execution.controllers;

import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.services.DatabaseFillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service/execution/db")
@CrossOrigin(origins = "*")
@Slf4j
public class DatabaseFillingServiceController {
  private final ConfigurationService configurationService;
  private final DatabaseFillingService databaseFillingService;

  public DatabaseFillingServiceController(
      ConfigurationService configurationService, DatabaseFillingService databaseFillingService) {
    this.configurationService = configurationService;
    this.databaseFillingService = databaseFillingService;
  }

  @GetMapping("fill")
  public String fill() {
    try {
      if (configurationService.getValueAsBoolean(ConfigurationKey.PRODUCTION_MODE)) {
        return "Application running in production mode: DB filling with random data disabled";
      }
      databaseFillingService.fill();
      return "DB filled with random data";
    } catch (Throwable e) {
      log.error(String.format("Error executing fill endpoint: %s", e.getMessage()), e);
      return String.format("DB filling failed: %s", e.getMessage());
    }
  }
}
