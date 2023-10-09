package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.internal.FtpChannelAdapter;

public class FtpExtractor extends ChannelExtractor {

  public FtpExtractor() {
    super(new FtpChannelAdapter(), f -> f.endsWith(".csv"));
  }

  FtpExtractor(ChannelAdapter channelAdapter) {
    super(channelAdapter, f -> f.endsWith(".csv"));
  }
}
