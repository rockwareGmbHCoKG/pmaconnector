package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.internal.SftpChannelAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SftpExtractor extends ChannelExtractor {

  public SftpExtractor() {
    super(new SftpChannelAdapter(), f -> f.endsWith(".csv"));
  }

  SftpExtractor(ChannelAdapter channelAdapter) {
    super(channelAdapter, f -> f.endsWith(".csv"));
  }
}
