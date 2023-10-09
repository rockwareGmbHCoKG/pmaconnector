package de.rockware.pma.connector.execution.tasks;

import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ExtractionType;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.services.ExecutionService;
import de.rockware.pma.connector.startup.status.StartupStatusRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExecutionScheduledTask {

  private final ConfigurationService configurationService;
  private final ExecutionService executionService;

  public ExecutionScheduledTask(
      ConfigurationService configurationService, ExecutionService executionService) {
    this.configurationService = configurationService;
    this.executionService = executionService;
  }

  @Scheduled(fixedRate = 60000, initialDelay = 60000)
  public void run() {
    if (!StartupStatusRegistry.getInstance().isStartupCompleted()) {
      log.debug("Application startup not completed yet: skipped execution");
      return;
    }
    if (!ExtractionType.getByValue(configurationService.getValue(ConfigurationKey.EXTRACTION_TYPE))
        .isScheduledExecution()) {
      return;
    }
    executionService.run();
  }
}
