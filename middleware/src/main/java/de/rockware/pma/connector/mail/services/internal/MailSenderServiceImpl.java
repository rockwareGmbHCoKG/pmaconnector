package de.rockware.pma.connector.mail.services.internal;

import de.rockware.pma.connector.common.converters.ExceptionConverter;
import de.rockware.pma.connector.common.utils.MailRecipientsMerger;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.beans.ExecutionError;
import de.rockware.pma.connector.mail.senders.MailSenderFactory;
import de.rockware.pma.connector.mail.services.MailSenderService;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.mail.internet.MimeMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

@Service
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {

  private final ConfigurationService configurationService;
  private final MailSenderFactory mailSenderFactory;
  private final String logDirPath;
  private final String zippedLogDirPath;

  @Autowired
  public MailSenderServiceImpl(
      ConfigurationService configurationService, MailSenderFactory mailSenderFactory) {
    this(configurationService, mailSenderFactory, "/log_dir", "/");
  }

  MailSenderServiceImpl(
      ConfigurationService configurationService,
      MailSenderFactory mailSenderFactory,
      String logDirPath,
      String zippedLogDirPath) {
    this.configurationService = configurationService;
    this.mailSenderFactory = mailSenderFactory;
    this.logDirPath = logDirPath;
    this.zippedLogDirPath = zippedLogDirPath;
  }

  @Override
  public void send(
      String subject, List<String> additionalRecipients, List<ExecutionError> executionErrors) {
    MailSenderContext mailSenderContext = new MailSenderContext();
    try {
      if (isMailSendingDisabled()) {
        return;
      }
      if (Objects.isNull(executionErrors) || executionErrors.isEmpty()) {
        return;
      }
      StringBuilder textBuilder =
          new StringBuilder("<h1>Some errors occurred during execution:</h1>");
      executionErrors.forEach(e -> addExecutionError(textBuilder, e));
      send(mailSenderContext, subject, additionalRecipients, textBuilder.toString());
    } catch (Throwable e) {
      log.error(String.format("Error executing sendMail: %s", e.getMessage()), e);
    } finally {
      mailSenderContext.cleanup();
    }
  }

  @Override
  public void send(
      String subject,
      List<String> additionalRecipients,
      String text,
      @Nullable Throwable exception) {
    if (isMailSendingDisabled()) {
      return;
    }
    if (StringUtils.isEmpty(text) && Objects.isNull(exception)) {
      log.debug("No text nor exception: skipped");
      return;
    }
    MailSenderContext mailSenderContext = new MailSenderContext();
    try {
      StringBuilder textBuilder = new StringBuilder();
      textBuilder.append("<h1>").append(text).append("</h1>");
      if (Objects.isNull(exception)) {
        textBuilder.append("<i>Stacktrace not present.</i><hr>");
      } else {
        printStackTrace(textBuilder, exception);
      }
      send(mailSenderContext, subject, additionalRecipients, textBuilder.toString());
    } catch (Throwable e) {
      log.error(String.format("Error executing sendMail: %s", e.getMessage()), e);
    } finally {
      mailSenderContext.cleanup();
    }
  }

  private void send(
      @NonNull MailSenderContext mailSenderContext,
      String subject,
      List<String> additionalRecipients,
      String text)
      throws Exception {
    JavaMailSender mailSender =
        mailSenderFactory.create(
            configurationService.getValue(ConfigurationKey.MAIL_SMTP_HOST),
            configurationService.getValueAsInt(ConfigurationKey.MAIL_SMTP_PORT),
            configurationService.getValue(ConfigurationKey.MAIL_USERNAME),
            configurationService.getValue(ConfigurationKey.MAIL_PASSWORD),
            configurationService.getValue(ConfigurationKey.MAIL_SMTP_TIMEOUT),
            configurationService.getValue(ConfigurationKey.MAIL_SMTP_SOCKET_FACTORY_PORT),
            configurationService.getValue(ConfigurationKey.MAIL_SMTP_SOCKET_FACTORY_CLASS));
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(configurationService.getValue(ConfigurationKey.MAIL_SENDER));
    checkRecipients(
        MailRecipientsMerger.merge(
            configurationService.getValue(ConfigurationKey.MAIL_TO_RECIPIENTS),
            additionalRecipients),
        helper::setTo);
    checkRecipients(
        configurationService.getValue(ConfigurationKey.MAIL_CC_RECIPIENTS), helper::setCc);
    checkRecipients(
        configurationService.getValue(ConfigurationKey.MAIL_BCC_RECIPIENTS), helper::setBcc);
    helper.setSubject(subject);
    helper.setText(text, true);
    if (configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ATTACH_LOGS)) {
      attachLogs(mailSenderContext, helper);
    }
    mailSender.send(message);
  }

  private void checkRecipients(String recipients, RecipientsSetter recipientsSetter)
      throws Exception {
    if (StringUtils.isEmpty(recipients)) {
      return;
    }
    String[] splittedRecipients = StringUtils.split(recipients, ",");
    if (Objects.isNull(splittedRecipients) || splittedRecipients.length == 0) {
      return;
    }
    recipientsSetter.set(splittedRecipients);
  }

  private boolean isMailSendingDisabled() {
    if (!configurationService.getValueAsBoolean(ConfigurationKey.MAIL_SENDING_ENABLED)) {
      log.debug("Mail sending disabled");
      return true;
    }
    return false;
  }

  private void addExecutionError(StringBuilder textBuilder, ExecutionError executionError) {
    textBuilder.append("<h3>").append(executionError.getMessage()).append("</h3>");
    textBuilder
        .append("Step: ")
        .append(executionError.getStep())
        .append(", campaign ID: \"")
        .append(executionError.getInfo().getCampaignId())
        .append("\", delivery ID: \"")
        .append(executionError.getInfo().getDeliveryId())
        .append("\", resource: \"")
        .append(executionError.getInfo().getResourceName());
    if (Objects.nonNull(executionError.getNewResourceName())) {
      textBuilder
          .append("\" (renamed to \"")
          .append(executionError.getNewResourceName())
          .append("\")");
    }
    textBuilder.append("<hr>");
    if (Objects.isNull(executionError.getThrowable())) {
      textBuilder.append("<i>Stack trace not present.</i><hr>");
      return;
    }
    printStackTrace(textBuilder, executionError.getThrowable());
  }

  private void printStackTrace(StringBuilder textBuilder, Throwable exception) {
    textBuilder.append("<pre>").append(ExceptionConverter.convert(exception)).append("</pre>");
    textBuilder.append("<hr>");
  }

  private void attachLogs(MailSenderContext mailSenderContext, MimeMessageHelper helper) {
    try {
      File zippedLogsFile =
          new File(
              new File(zippedLogDirPath),
              String.format("log_dir_zipped_%d.zip", new Date().getTime()));
      ZipUtil.pack(new File(logDirPath), zippedLogsFile);
      helper.addAttachment("logs.zip", zippedLogsFile);
      mailSenderContext.setZippedLogsFile(zippedLogsFile);
    } catch (Throwable e) {
      log.error(String.format("Error zipping logs: %s", e.getMessage()), e);
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  private static class MailSenderContext {

    private File zippedLogsFile;

    public void cleanup() {
      if (Objects.isNull(zippedLogsFile) || !zippedLogsFile.exists()) {
        return;
      }
      log.debug(String.format("Removing zipped logs file: %s", zippedLogsFile.delete()));
    }
  }

  private interface RecipientsSetter {
    void set(String[] recipients) throws Exception;
  }
}
