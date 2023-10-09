package de.rockware.pma.connector.channels.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.extraction.beans.ChannelResource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
abstract class AbstractChannelAdapter implements ChannelAdapter {

  <T> void processFiles(
      Collection<T> entries,
      ResourceNameProvider<T> resourceNameProvider,
      ResourceRetriever resourceRetriever,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback)
      throws Exception {
    if (Objects.isNull(entries) || entries.isEmpty()) {
      log.debug("No entries: extraction terminated");
      return;
    }
    for (T entry : entries) {
      if (Objects.isNull(entry)) {
        log.debug("Null entry: skipped");
        continue;
      }
      String resourceName = resourceNameProvider.getName(entry);
      if (StringUtils.isEmpty(resourceName)) {
        log.debug("Empty resource name: skipped");
        continue;
      }
      if (!resourceNameFilter.passesFilter(resourceName)) {
        log.debug("Resource name filter not passed: skipped");
        continue;
      }
      callback.onRetrieval(
          new ChannelResource(resourceName, resourceRetriever.retrieve(resourceName)));
      log.debug(String.format("Resource processed: %s", resourceName));
    }
  }

  interface ResourceRetriever {
    InputStream retrieve(String resourceName) throws Exception;
  }

  interface ResourceNameProvider<T> {
    String getName(T resource);
  }
}
