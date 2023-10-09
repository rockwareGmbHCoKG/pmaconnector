package de.rockware.pma.connector.mail.services;

import de.rockware.pma.connector.execution.beans.ExecutionError;
import java.util.List;
import org.springframework.lang.Nullable;

public interface MailSenderService {

  void send(
      String subject, List<String> additionalRecipients, List<ExecutionError> executionErrors);

  void send(
      String subject,
      List<String> additionalRecipients,
      String text,
      @Nullable Throwable exception);
}
