package de.rockware.pma.connector.channels.internal;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class SftpChannelAdapter extends AbstractChannelAdapter {

  @Override
  public void retrieve(
      Collection<ConfigurationValue> configurationValues,
      ResourceNameFilter resourceNameFilter,
      ResourceCallback callback) {
    try {
      ChannelSftp channelSftp = initChannel(configurationValues);
      processFiles(
          getLsEntries(channelSftp),
          LsEntry::getFilename,
          channelSftp::get,
          resourceNameFilter,
          callback);
      log.debug(String.format("Resources retrieved from %s directory", channelSftp.pwd()));
      channelSftp.exit();
    } catch (Throwable e) {
      throw new SftpChannelAdapterException(
          String.format("Error retrieving resources: %s", e.getMessage()), e);
    }
  }

  @Override
  public void rename(
      Collection<ConfigurationValue> configurationValues,
      String resourceName,
      String newResourceName) {
    try {
      ChannelSftp channelSftp = initChannel(configurationValues);
      String pwd = channelSftp.pwd();
      channelSftp.rename(
          String.format("%s/%s", pwd, resourceName), String.format("%s/%s", pwd, newResourceName));
      log.debug(
          String.format(
              "Resource %s renamed to %s in %s directory", resourceName, newResourceName, pwd));
      channelSftp.exit();
    } catch (Throwable e) {
      throw new SftpChannelAdapterException(
          String.format(
              "Error renaming resource %s to %s: %s",
              resourceName, newResourceName, e.getMessage()),
          e);
    }
  }

  @Override
  public void delete(Collection<ConfigurationValue> configurationValues, String resourceName) {
    try {
      ChannelSftp channelSftp = initChannel(configurationValues);
      String resourceDetails =
          Optional.ofNullable(channelSftp.ls(resourceName)).map(Vector::toString).orElse(null);
      if (StringUtils.isNotEmpty(resourceDetails) && resourceDetails.contains(resourceName)) {
        channelSftp.rm(resourceName);
        log.debug(
            String.format(
                "Resource %s removed from %s directory", resourceName, channelSftp.pwd()));
      }
      channelSftp.exit();
    } catch (Throwable e) {
      throw new SftpChannelAdapterException(
          String.format("Error deleting resource %s: %s", resourceName, e.getMessage()), e);
    }
  }

  @Override
  public void write(
      Collection<ConfigurationValue> configurationValues, String resourceName, File file) {
    try {
      ChannelSftp channelSftp = initChannel(configurationValues);
      String pwd = channelSftp.pwd();
      channelSftp.put(
          Files.newInputStream(file.toPath()), String.format("%s/%s", pwd, resourceName));
      log.debug(
          String.format("Resource %s removed from %s directory", resourceName, channelSftp.pwd()));
      channelSftp.exit();
    } catch (Throwable e) {
      throw new SftpChannelAdapterException(
          String.format("Error deleting resource %s: %s", resourceName, e.getMessage()), e);
    }
  }

  private ChannelSftp initChannel(Collection<ConfigurationValue> configurationValues)
      throws JSchException, SftpException {
    JSch jsch = new JSch();
    Session jschSession =
        jsch.getSession(
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.EXTRACTION_SFTP_USERNAME),
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.EXTRACTION_SFTP_HOST),
            ConfigurationValueRetriever.getAsInt(
                configurationValues, ConfigurationKey.EXTRACTION_SFTP_PORT));
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    jschSession.setConfig(config);
    jschSession.setPassword(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.EXTRACTION_SFTP_PASSWORD));
    jschSession.connect(
        ConfigurationValueRetriever.getAsInt(
            configurationValues, ConfigurationKey.EXTRACTION_SFTP_SESSION_TIMEOUT));
    Channel sftp = jschSession.openChannel("sftp");
    sftp.connect(
        ConfigurationValueRetriever.getAsInt(
            configurationValues, ConfigurationKey.EXTRACTION_SFTP_CHANNEL_TIMEOUT));
    ChannelSftp channelSftp = (ChannelSftp) sftp;
    channelSftp.cd(
        ConfigurationValueRetriever.get(
            configurationValues, ConfigurationKey.EXTRACTION_SFTP_PATH));
    return channelSftp;
  }

  private List<LsEntry> getLsEntries(ChannelSftp channelSftp) throws SftpException {
    List<LsEntry> entries = new ArrayList<>();
    for (Object entry : channelSftp.ls(".")) {
      if (Objects.isNull(entry) || !entry.getClass().isAssignableFrom(LsEntry.class)) {
        continue;
      }
      entries.add((LsEntry) entry);
    }
    return entries;
  }

  private static class SftpChannelAdapterException extends RuntimeException {

    public SftpChannelAdapterException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
