package de.rockware.pma.connector.mail.senders.internal;

import de.rockware.pma.connector.mail.senders.MailSenderFactory;
import java.util.Properties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailSenderFactoryImpl implements MailSenderFactory {

  @Override
  public JavaMailSender create(
      String host,
      int port,
      String username,
      String password,
      String timeout,
      String socketFactoryPort,
      String socketFactoryClass) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.timeout", timeout);
    props.put("mail.smtp.writetimeout", timeout);
    props.put("mail.smtp.socketFactory.port", socketFactoryPort);
    props.put("mail.smtp.socketFactory.class", socketFactoryClass);
    props.put("mail.debug", "true");
    return mailSender;
  }
}
