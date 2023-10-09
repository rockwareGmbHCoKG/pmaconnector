package de.rockware.pma.connector.configuration.providers.internal;

import de.rockware.pma.connector.configuration.beans.KeyValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.providers.ConfigurationProvider;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;

public class LocalToPmaTestConfigurationProvider implements ConfigurationProvider {

  @Override
  public Collection<KeyValue> create(Map<ConfigurationKey, KeyValue> defaultConfiguration) {
    override(defaultConfiguration, ConfigurationKey.MAIL_USERNAME, "<mail_username>");
    override(defaultConfiguration, ConfigurationKey.MAIL_PASSWORD, "<mail_password>");
    override(defaultConfiguration, ConfigurationKey.MAIL_SENDER, "<mail_sender>");
    override(defaultConfiguration, ConfigurationKey.MAIL_SMTP_HOST, "<mail_smtp_host>");
    override(
        defaultConfiguration,
        ConfigurationKey.MAIL_TO_RECIPIENTS,
        "<comma_separated_recipients_to>");
    override(
        defaultConfiguration,
        ConfigurationKey.MAIL_CC_RECIPIENTS,
        "<comma_separated_recipients_cc>");
    override(
        defaultConfiguration,
        ConfigurationKey.MAIL_BCC_RECIPIENTS,
        "<comma_separated_recipients_bcc>");
    override(defaultConfiguration, ConfigurationKey.MAIL_SENDING_ENABLED, "true");
    override(defaultConfiguration, ConfigurationKey.MAIL_SENDING_ATTACH_LOGS, "true");
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_TYPE, "PUSH");
    override(defaultConfiguration, ConfigurationKey.PRODUCTION_MODE, Boolean.FALSE.toString());
    String localHost = getLocalHost();
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_SFTP_HOST, localHost);
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_FTP_HOST, localHost);
    override(defaultConfiguration, ConfigurationKey.PMA_PARTNER_SYSTEM_ID, "<partner_system_id>");
    override(
        defaultConfiguration,
        ConfigurationKey.PMA_PARTNER_SYSTEM_CUSTOMER_ID,
        "<partner_system_customer_id>");
    override(
        defaultConfiguration,
        ConfigurationKey.PMA_AUTHENTICATION_SECRET,
        "<pma_authentication_secret>");
    override(defaultConfiguration, ConfigurationKey.PMA_AUTHENTICATION_LOCALE, "en");
    override(
        defaultConfiguration,
        ConfigurationKey.PMA_SERVICE_BASE_URL,
        "https://api-uat.dhl.com/post/advertising/print-mailing");
    override(
        defaultConfiguration,
        ConfigurationKey.PMA_REDIRECT_FRONTEND_BASE_URL,
        "https://uat.print-mailing-test.deutschepost.de");
    override(defaultConfiguration, ConfigurationKey.PMA_REDIRECT_SECRET, "<pma_redirect_secret>");
    override(defaultConfiguration, ConfigurationKey.PMA_REDIRECT_FIRSTNAME, "-");
    override(defaultConfiguration, ConfigurationKey.PMA_REDIRECT_LASTNAME, "-");
    override(defaultConfiguration, ConfigurationKey.PMA_REDIRECT_EMAIL, "<pma_redirect_email>");
    override(
        defaultConfiguration, ConfigurationKey.PMA_REDIRECT_USERNAME, "<pma_redirect_username>");
    override(defaultConfiguration, ConfigurationKey.PMA_REDIRECT_ISS, "<pma_redirect_iss>");
    return defaultConfiguration.values();
  }

  private String getLocalHost() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (Throwable e) {
      return "localhost";
    }
  }
}
