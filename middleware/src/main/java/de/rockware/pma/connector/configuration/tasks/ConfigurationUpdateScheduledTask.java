package de.rockware.pma.connector.configuration.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.factories.DefaultsFactory;
import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.mail.services.MailSenderService;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.startup.status.StartupStatusRegistry;
import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConfigurationUpdateScheduledTask {

  private final ConfigurationService configurationService;
  private final ReadWriteService<Mapping> mappingService;
  private final MailSenderService mailSenderService;
  private final String configDirPath;

  @Autowired
  public ConfigurationUpdateScheduledTask(
      ConfigurationService configurationService,
      ReadWriteService<Mapping> mappingService,
      MailSenderService mailSenderService) {
    this(configurationService, mappingService, mailSenderService, Constants.SHARED_DIR_FOLDER_PATH);
  }

  ConfigurationUpdateScheduledTask(
      ConfigurationService configurationService,
      ReadWriteService<Mapping> mappingService,
      MailSenderService mailSenderService,
      String configDirPath) {
    this.configurationService = configurationService;
    this.mappingService = mappingService;
    this.mailSenderService = mailSenderService;
    this.configDirPath = configDirPath;
  }

  @EventListener
  public void onApplicationStartup(ContextRefreshedEvent event) {
    update();
    Collection<ConfigurationValue> configurationValues = configurationService.getAll();
    if (Objects.isNull(configurationValues) || configurationValues.isEmpty()) {
      configurationService.setAll(
          DefaultsFactory.createConfigurationValues().toArray(new ConfigurationValue[0]));
      log.debug("Default configuration written");
    }
    Collection<Mapping> mappings = mappingService.getAll();
    if (Objects.isNull(mappings) || mappings.isEmpty()) {
      mappingService.setAll(DefaultsFactory.createMappings().toArray(new Mapping[0]));
      log.debug("Default mappings written");
    }
    StartupStatusRegistry.getInstance().setStartupCompleted(true);
  }

  @Scheduled(fixedRate = 60000, initialDelay = 60000)
  public void update() {
    try {
      File dir = new File(configDirPath);
      if (!dir.exists() || !dir.isDirectory()) {
        String message = "Configuration directory does not exist";
        log.debug(message);
        mailSenderService.send("Configuration check error", Collections.emptyList(), message, null);
        return;
      }
      String[] list = dir.list();
      if (Objects.isNull(list) || list.length == 0) {
        return;
      }
      loadCollection(
          dir,
          "configuration.json",
          ConfigurationValue[].class,
          configurationService::setAll,
          f -> delete(ConfigurationKey.DELETE_IMPORTED_CONFIGURATION_FILE, f));
      loadCollection(
          dir,
          "mappings.json",
          Mapping[].class,
          mappingService::setAll,
          f -> delete(ConfigurationKey.DELETE_IMPORTED_MAPPING_FILE, f));
    } catch (Throwable e) {
      String message = String.format("Error checking %s: %s", configDirPath, e.getMessage());
      log.error(message, e);
      mailSenderService.send("Configuration update error", Collections.emptyList(), message, e);
    }
  }

  private <T> void loadCollection(
      File configDir,
      String filename,
      Class<T> valueType,
      Consumer<T> writer,
      Consumer<File> fileLoadingCompletedConsumer) {
    try {
      File file = new File(configDir, filename);
      if (!file.exists()) {
        return;
      }
      ObjectMapper objectMapper = new ObjectMapper();
      T value = objectMapper.readValue(Files.newInputStream(file.toPath()), valueType);
      writer.accept(value);
      log.debug(String.format("\"%s\" folder successfully load", configDir.getName()));
      fileLoadingCompletedConsumer.accept(file);
    } catch (Throwable e) {
      String message =
          String.format("Error processing \"%s\" folder: %s", configDir.getName(), e.getMessage());
      log.error(message, e);
      mailSenderService.send("Configuration update error", Collections.emptyList(), message, e);
    }
  }

  public void delete(ConfigurationKey deleteFileConfigurationKey, File file) {
    if (!configurationService.getValueAsBoolean(deleteFileConfigurationKey)) {
      return;
    }
    log.debug(String.format("File \"%s\" deleted: %s", file.getName(), file.delete()));
  }
}
