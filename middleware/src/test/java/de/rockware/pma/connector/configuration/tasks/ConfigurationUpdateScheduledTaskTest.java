package de.rockware.pma.connector.configuration.tasks;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static de.rockware.pma.connector.common.utils.TestBeansFactory.createMappings;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.mail.services.MailSenderService;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.startup.status.StartupStatusRegistry;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

@ExtendWith({MockitoExtension.class})
class ConfigurationUpdateScheduledTaskTest {

  @Mock private ConfigurationService configurationService;
  @Mock private ReadWriteService<Mapping> mappingService;
  @Mock private MailSenderService mailSenderService;

  private File configDir;
  private ConfigurationUpdateScheduledTask sut;

  @BeforeEach
  void setUp() throws Exception {
    StartupStatusRegistry.getInstance().setStartupCompleted(false);
    this.configDir = Files.createTempDirectory("config").toFile();
    this.sut =
        new ConfigurationUpdateScheduledTask(
            configurationService, mappingService, mailSenderService, configDir.getAbsolutePath());
  }

  @AfterEach
  void tearDown() {
    FileSystemUtils.deleteRecursively(configDir);
  }

  @Test
  void exceptionThrown() {
    FileSystemUtils.deleteRecursively(configDir);
    doThrow(new RuntimeException("Test exception"))
        .when(mailSenderService)
        .send(anyString(), anyList(), anyString(), any());
    Assertions.assertThrows(RuntimeException.class, () -> sut.update());
    verifyNoInteractions(configurationService, mappingService);
    verify(mailSenderService, times(2)).send(anyString(), anyList(), anyString(), any());
  }

  @Test
  void notExistingDirectory() {
    FileSystemUtils.deleteRecursively(configDir);
    sut.update();
    verifyNoInteractions(configurationService, mappingService);
    verify(mailSenderService).send(anyString(), anyList(), anyString(), isNull());
  }

  @Test
  void noFiles() {
    sut.update();
    verifyNoInteractions(configurationService, mappingService, mailSenderService);
  }

  @Test
  void configurationFound() {
    writeToFile(createConfigurationValues(), "configuration.json");
    sut.update();
    verify(configurationService).setAll(any());
    verifyNoInteractions(mappingService, mailSenderService);
  }

  @Test
  void mappingsFound() {
    writeToFile(createMappings(), "mappings.json");
    sut.update();
    verify(mappingService).setAll(any());
    verify(configurationService)
        .getValueAsBoolean(eq(ConfigurationKey.DELETE_IMPORTED_MAPPING_FILE));
    verifyNoMoreInteractions(configurationService);
    verifyNoInteractions(mailSenderService);
  }

  @Test
  void allFound() {
    writeToFile(createConfigurationValues(), "configuration.json");
    writeToFile(createMappings(), "mappings.json");
    sut.update();
    verify(configurationService).setAll(any());
    verify(mappingService).setAll(any());
    verify(configurationService, times(2)).getValueAsBoolean(any());
    verifyNoMoreInteractions(configurationService);
    verifyNoInteractions(mailSenderService);
  }

  private void writeToFile(List<? extends Serializable> records, String filename) {
    try {
      PrintWriter writer =
          new PrintWriter(
              new File(configDir, filename).getAbsolutePath(), StandardCharsets.UTF_8.name());
      writer.write(BeanConverter.serializeCollection(records));
      writer.close();
    } catch (Throwable e) {
      // do nothing
    }
  }
}
