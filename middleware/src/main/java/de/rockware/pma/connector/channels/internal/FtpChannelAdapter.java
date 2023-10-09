package de.rockware.pma.connector.channels.internal;

import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

@Slf4j
public class FtpChannelAdapter extends AbstractChannelAdapter {

  @Override
  public void retrieve(
      Collection<ConfigurationValue> configurationValues,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback) {
    Set<File> tempFiles = new HashSet<>();
    try {
      FTPClient ftpClient = init(configurationValues);
      processFiles(
          Arrays.stream(ftpClient.listFiles()).collect(Collectors.toList()),
          FTPFile::getName,
          resourceName -> {
            File file = Files.createTempFile("extraction", ".data").toFile();
            tempFiles.add(file);
            ftpClient.retrieveFile(resourceName, Files.newOutputStream(file.toPath()));
            return Files.newInputStream(file.toPath());
          },
          resourceNameFilter,
          callback);
      log.debug("Resources retrieved from configured FTP directory");
      ftpClient.disconnect();
    } catch (Throwable e) {
      throw new FtpChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    } finally {
      for (File tempFile : tempFiles) {
        if (!tempFile.delete()) {
          log.debug(String.format("File %s not deleted", tempFile.getName()));
        }
      }
    }
  }

  @Override
  public void rename(
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      String newResourceName) {
    try {
      FTPClient ftpClient = init(configurationValues);
      if (ftpClient.rename(resourceName, newResourceName)) {
        log.debug(
            String.format(
                "Resource %s renamed to %s in configured FTP directory",
                resourceName, newResourceName));
      }
      ftpClient.disconnect();
    } catch (Throwable e) {
      throw new FtpChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void delete(Collection<ConfigurationValue> configurationValues, String resourceName) {
    try {
      FTPClient ftpClient = init(configurationValues);
      if (ftpClient.deleteFile(resourceName)) {
        log.debug(String.format("Resource %s deleted successfully", resourceName));
      }
      ftpClient.disconnect();
    } catch (Throwable e) {
      throw new FtpChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void write(
      Collection<ConfigurationValue> configurationValues, String resourceName, File file) {
    try {
      FTPClient ftpClient = init(configurationValues);
      if (ftpClient.storeFile(resourceName, Files.newInputStream(file.toPath()))) {
        log.debug("Resource %s created successfully");
      }
      ftpClient.disconnect();
    } catch (Throwable e) {
      throw new FtpChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  private FTPClient init(Collection<ConfigurationValue> configurationValues) throws Exception {
    FTPClient ftpClient = new FTPClient();
    ftpClient.connect(
        ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.EXTRACTION_FTP_HOST),
        ConfigurationValueRetriever.getAsInt(
            configurationValues, ConfigurationKey.EXTRACTION_FTP_PORT));
    int reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftpClient.disconnect();
      throw new IOException("Exception connecting to FTP Server");
    }
    if (!ConfigurationValueRetriever.getAsBoolean(
        configurationValues, ConfigurationKey.EXTRACTION_FTP_ACTIVE_MODE)) {
      ftpClient.enterLocalPassiveMode();
      ftpClient.setRemoteVerificationEnabled(false);
    }
    ftpClient.login(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.EXTRACTION_FTP_USERNAME),
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.EXTRACTION_FTP_PASSWORD));
    String path =
        ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.EXTRACTION_FTP_PATH);
    if (!ftpClient.changeWorkingDirectory(path)) {
      throw new IOException(String.format("Error changing working directory to %s", path));
    }
    return ftpClient;
  }

  private static class FtpChannelAdapterException extends RuntimeException {
    public FtpChannelAdapterException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
