package de.rockware.pma.connector.mail.services.internal;

import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.converters.StringConverter;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey.ConfigurationGroup;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey.ConfigurationValueType;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.beans.ExecutionError;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.mail.senders.MailSenderFactory;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.FileSystemUtils;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceImplTest {
  @Mock private ConfigurationService configurationService;
  @Mock private MailSenderFactory mailSenderFactory;
  @Mock private JavaMailSender javaMailSender;
  @Mock private MimeMessage mimeMessage;

  private File logDir;

  private MailSenderServiceImpl sut;

  @BeforeEach
  void setUp() throws Exception {
    doAnswer(
            i -> {
              ConfigurationKey key = i.getArgument(0, ConfigurationKey.class);
              if (ConfigurationGroup.MAIL.equals(key.getGroup())) {
                return key.getDefaultValue();
              }
              return "";
            })
        .when(configurationService)
        .getValue(any());
    doAnswer(
            i -> {
              ConfigurationKey key = i.getArgument(0, ConfigurationKey.class);
              if (ConfigurationGroup.MAIL.equals(key.getGroup())
                  && ConfigurationValueType.INTEGER.equals(key.getValueType())) {
                return StringConverter.toInt(key.getDefaultValue());
              }
              return 0;
            })
        .when(configurationService)
        .getValueAsInt(any());
    doAnswer(
            i -> {
              ConfigurationKey key = i.getArgument(0, ConfigurationKey.class);
              if (ConfigurationGroup.MAIL.equals(key.getGroup())
                  && ConfigurationValueType.BOOLEAN.equals(key.getValueType())) {
                return StringConverter.toBoolean(key.getDefaultValue());
              }
              return 0;
            })
        .when(configurationService)
        .getValueAsBoolean(any());
    when(mailSenderFactory.create(
            anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(javaMailSender);
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    this.logDir = Files.createTempDirectory("logs").toFile();
    Files.createFile(new File(logDir, "log-file.log").toPath());
    this.sut =
        new MailSenderServiceImpl(
            configurationService,
            mailSenderFactory,
            logDir.getAbsolutePath(),
            System.getProperty("java.io.tmpdir"));
  }

  @AfterEach
  void tearDown() {
    FileSystemUtils.deleteRecursively(logDir);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void sendExecutionErrorsMailSendingDisabled() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(false);
    sut.send("Test subject", Collections.emptyList(), createExecutionErrors());
    verifyNoInteractions(mailSenderFactory);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void sendExecutionErrorsNoExecutionErrors() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(false);
    sut.send("Test subject", Collections.emptyList(), null);
    verifyNoInteractions(mailSenderFactory);
  }

  @Test
  void sendExecutionErrorsWithoutLogs() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(true);
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ATTACH_LOGS))
        .thenReturn(false);
    sut.send("Test subject", Collections.emptyList(), createExecutionErrors());
    verify(mailSenderFactory)
        .create(
            anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString());
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(isA(MimeMessage.class));
  }

  @Test
  void sendExecutionErrorsWithLogs() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(true);
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ATTACH_LOGS))
        .thenReturn(true);
    sut.send("Test subject", Collections.emptyList(), createExecutionErrors());
    verify(mailSenderFactory)
        .create(
            anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString());
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(isA(MimeMessage.class));
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void sendExceptionMailSendingDisabled() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(false);
    sut.send(
        "Test subject",
        Collections.emptyList(),
        "Test exception occured",
        new RuntimeException("Test exception!"));
    verifyNoInteractions(mailSenderFactory);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void sendExceptionNoTextNorException() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(false);
    sut.send("Test subject", null, null);
    verifyNoInteractions(mailSenderFactory);
  }

  @Test
  void sendExceptionWithoutLogs() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(true);
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ATTACH_LOGS))
        .thenReturn(false);
    sut.send(
        "Test subject",
        Collections.emptyList(),
        "Test exception occured",
        new RuntimeException("Test exception!"));
    verify(mailSenderFactory)
        .create(
            anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString());
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(isA(MimeMessage.class));
  }

  @Test
  void sendExceptionWithLogs() {
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED))
        .thenReturn(true);
    when(configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ATTACH_LOGS))
        .thenReturn(true);
    sut.send(
        "Test subject",
        Collections.emptyList(),
        "Test exception occured",
        new RuntimeException("Test exception!"));
    verify(mailSenderFactory)
        .create(
            anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString());
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(isA(MimeMessage.class));
  }

  private List<ExecutionError> createExecutionErrors() {
    ArrayList<ExecutionError> executionErrors = new ArrayList<>();
    executionErrors.add(
        new ExecutionError(
            new Info(
                "TEST_RESOURCE_1",
                "CAMPAIGN1",
                "Campaign 1",
                "DELIVERY1",
                "Delivery 1",
                Date.from(
                    LocalDate.of(2021, 9, 1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                Date.from(
                    LocalDate.of(2021, 12, 31)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                true,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false),
            Step.EXTRACTION,
            "Error with resource 1"));
    executionErrors.add(
        new ExecutionError(
            new Info(
                "TEST_RESOURCE_1",
                "CAMPAIGN1",
                "Campaign 1",
                "DELIVERY1",
                "Delivery 1",
                Date.from(
                    LocalDate.of(2021, 9, 1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                Date.from(
                    LocalDate.of(2021, 12, 31)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                true,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false),
            Step.TRANSFORMATION,
            null,
            "Error with stacktrace",
            new RuntimeException("Test error 1!")));
    executionErrors.add(
        new ExecutionError(
            new Info(
                "TEST_RESOURCE_2",
                "CAMPAIGN1",
                "Campaign 1",
                "DELIVERY1",
                "Delivery 1",
                Date.from(
                    LocalDate.of(2021, 9, 1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                Date.from(
                    LocalDate.of(2021, 12, 31)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                true,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false),
            Step.LOAD,
            null,
            "Error with resource 2",
            new RuntimeException("Test error 2!")));
    return executionErrors;
  }
}
