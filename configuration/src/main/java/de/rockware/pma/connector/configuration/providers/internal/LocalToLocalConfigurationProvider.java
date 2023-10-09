package de.rockware.pma.connector.configuration.providers.internal;

import de.rockware.pma.connector.configuration.beans.KeyValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.providers.ConfigurationProvider;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;

public class LocalToLocalConfigurationProvider implements ConfigurationProvider {

  @Override
  public Collection<KeyValue> create(Map<ConfigurationKey, KeyValue> defaultConfiguration) {
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_TYPE, "PUSH");
    override(defaultConfiguration, ConfigurationKey.PRODUCTION_MODE, Boolean.FALSE.toString());
    String localHost = getLocalHost();
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_SFTP_HOST, localHost);
    override(defaultConfiguration, ConfigurationKey.EXTRACTION_FTP_HOST, localHost);
    override(
        defaultConfiguration,
        ConfigurationKey.PMA_SERVICE_BASE_URL,
        String.format("http://%s:8081", localHost));
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
