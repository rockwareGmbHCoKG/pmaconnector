package de.rockware.pma.connector.channels.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import java.io.File;
import java.util.Collection;

public class FallbackChannelAdapter implements ChannelAdapter {
  @Override
  public void retrieve(
      Collection<ConfigurationValue> configurationValues,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback) {
    // do nothing
  }

  @Override
  public void rename(
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      String newResourceName) {
    // do nothing
  }

  @Override
  public void delete(Collection<ConfigurationValue> configurationValues, String resourceName) {
    // do nothing
  }

  @Override
  public void write(
      Collection<ConfigurationValue> configurationValues, String resourceName, File file) {
    // do nothing
  }
}
