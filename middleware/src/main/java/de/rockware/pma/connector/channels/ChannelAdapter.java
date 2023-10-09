package de.rockware.pma.connector.channels;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.extraction.beans.ChannelResource;
import java.io.File;
import java.util.Collection;

public interface ChannelAdapter {

  void retrieve(
      Collection<ConfigurationValue> configurationValues,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback);

  void rename(
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      String newResourceName);

  void delete(Collection<ConfigurationValue> configurationValues, String resourceName);

  void write(Collection<ConfigurationValue> configurationValues, String resourceName, File file);

  interface ResourceNameFilter {

    boolean passesFilter(String filename);
  }

  interface ResourceCallback {

    void onRetrieval(ChannelResource channelResource);
  }
}
