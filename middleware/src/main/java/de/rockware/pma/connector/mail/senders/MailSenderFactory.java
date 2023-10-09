package de.rockware.pma.connector.mail.senders;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailSenderFactory {

  JavaMailSender create(
      String host,
      int port,
      String username,
      String password,
      String timeout,
      String socketFactoryPort,
      String socketFactoryClass);
}
