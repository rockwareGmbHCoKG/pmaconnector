package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.internal.SharedDirChannelAdapter;

public class InternalExtractor extends ChannelExtractor {

  public InternalExtractor() {
    super(new SharedDirChannelAdapter(), f -> f.endsWith(".csv"));
  }

  InternalExtractor(ChannelAdapter channelAdapter) {
    super(channelAdapter, f -> f.endsWith(".csv"));
  }
}
