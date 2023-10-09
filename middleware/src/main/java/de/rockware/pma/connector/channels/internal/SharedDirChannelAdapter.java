package de.rockware.pma.connector.channels.internal;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharedDirChannelAdapter extends AbstractChannelAdapter {

  @Override
  public void retrieve(
      Collection<ConfigurationValue> configurationValues,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback) {
    try {
      File sharedDir = getSharedDir();
      processFiles(
          Optional.ofNullable(sharedDir.list()).map(Arrays::asList).orElse(Collections.emptyList()),
          n -> n,
          n -> Files.newInputStream(new File(sharedDir, n).toPath()),
          resourceNameFilter,
          callback);
    } catch (Throwable e) {
      throw new SharedDirChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void rename(
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      String newResourceName) {
    try {
      File sharedDir = getSharedDir();
      File toBeRenamed = new File(sharedDir, resourceName);
      boolean renamed = toBeRenamed.renameTo(new File(sharedDir, newResourceName));
      if (renamed) {
        log.debug(
            String.format(
                "Resource %s renamed to %s in %s directory",
                resourceName, newResourceName, Constants.SHARED_DIR_FOLDER_PATH));
      }
    } catch (Throwable e) {
      throw new SharedDirChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void delete(Collection<ConfigurationValue> configurationValues, String resourceName) {
    try {
      File sharedDir = getSharedDir();
      if (Files.deleteIfExists(new File(sharedDir, resourceName).toPath())) {
        log.debug(String.format("Resource %s deleted successfully", resourceName));
      }
    } catch (Throwable e) {
      throw new SharedDirChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void write(
      Collection<ConfigurationValue> configurationValues, String resourceName, File file) {
    try {
      File sharedDir = getSharedDir();
      if (Files.copy(
              Files.newInputStream(file.toPath()),
              new File(sharedDir, resourceName).toPath(),
              StandardCopyOption.REPLACE_EXISTING)
          != 0) {
        log.debug(String.format("Resource %s created successfully", resourceName));
      }
    } catch (Throwable e) {
      throw new SharedDirChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  private File getSharedDir() throws IOException {
    File sharedDir = new File(Constants.SHARED_DIR_FOLDER_PATH);
    if (!sharedDir.exists()) {
      throw new IOException(
          String.format("Error: %s does not exist!", Constants.SHARED_DIR_FOLDER_PATH));
    }
    return sharedDir;
  }

  private static class SharedDirChannelAdapterException extends RuntimeException {
    public SharedDirChannelAdapterException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
