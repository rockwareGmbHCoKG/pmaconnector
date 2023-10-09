package de.rockware.pma.connector.configuration.enumerations;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.internal.FallbackChannelAdapter;
import de.rockware.pma.connector.channels.internal.FtpChannelAdapter;
import de.rockware.pma.connector.channels.internal.SftpChannelAdapter;
import de.rockware.pma.connector.channels.internal.SharedDirChannelAdapter;
import de.rockware.pma.connector.common.retrievers.EnumRetriever;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.extraction.extractors.internal.*;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.lang.NonNull;

public enum ExtractionType {
  SFTP(c -> new SftpExtractor(), SftpChannelAdapter::new, true, true),
  FTP(c -> new FtpExtractor(), FtpChannelAdapter::new, true, true),
  INTERNAL(c -> new InternalExtractor(), SharedDirChannelAdapter::new, true, true),
  PUSH(c -> new PushExtractor(c.getInitialInfoList()), SharedDirChannelAdapter::new, false, false),
  TEST(c -> new TestExtractor(), FallbackChannelAdapter::new, true, true),
  UNDEFINED(c -> new UndefinedExtractor(), FallbackChannelAdapter::new, false, false);

  private final Function<ExecutionContext, Extractor> extractorFactory;
  private final Supplier<ChannelAdapter> channelAdapterFactory;
  private final boolean scheduledExecution;
  private final boolean updateInfo;

  ExtractionType(
      @NonNull Function<ExecutionContext, Extractor> extractorFactory,
      Supplier<ChannelAdapter> channelAdapterFactory,
      boolean scheduledExecution,
      boolean updateInfo) {
    this.extractorFactory = extractorFactory;
    this.channelAdapterFactory = channelAdapterFactory;
    this.scheduledExecution = scheduledExecution;
    this.updateInfo = updateInfo;
  }

  public Function<ExecutionContext, Extractor> getExtractorFactory() {
    return extractorFactory;
  }

  public Supplier<ChannelAdapter> getChannelAdapterFactory() {
    return channelAdapterFactory;
  }

  public boolean isScheduledExecution() {
    return scheduledExecution;
  }

  public boolean isUpdateInfo() {
    return updateInfo;
  }

  public static ExtractionType getByValue(String value) {
    return EnumRetriever.getByValue(value, values(), ExtractionType.UNDEFINED);
  }
}
