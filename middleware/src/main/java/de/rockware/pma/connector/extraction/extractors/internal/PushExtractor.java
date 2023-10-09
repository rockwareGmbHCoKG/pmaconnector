package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.ChannelAdapter.ResourceNameFilter;
import de.rockware.pma.connector.channels.internal.SharedDirChannelAdapter;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import java.util.List;
import java.util.Objects;

public class PushExtractor extends ChannelExtractor {

  public PushExtractor(List<Info> initialInfoList) {
    super(
        new SharedDirChannelAdapter(),
        new PushExtractorResourceNameFilter(initialInfoList),
        new PushExtractorInfoProvider());
  }

  PushExtractor(ChannelAdapter channelAdapter, List<Info> intialInfoList) {
    super(
        channelAdapter,
        new PushExtractorResourceNameFilter(intialInfoList),
        new PushExtractorInfoProvider());
  }

  private static class PushExtractorResourceNameFilter implements ResourceNameFilter {
    private final List<Info> initialInfoList;

    private PushExtractorResourceNameFilter(List<Info> initialInfoList) {
      this.initialInfoList = initialInfoList;
    }

    @Override
    public boolean passesFilter(String filename) {
      if (Objects.isNull(initialInfoList) || initialInfoList.isEmpty()) {
        return false;
      }
      return initialInfoList.stream().anyMatch(i -> i.getResourceName().equals(filename));
    }
  }

  private static class PushExtractorInfoProvider implements InfoProvider {

    @Override
    public Info getInfo(ExecutionContext executionContext, String resourceName) {
      return executionContext.getInitialInfoList().stream()
          .filter(
              i -> Objects.nonNull(i.getResourceName()) && i.getResourceName().equals(resourceName))
          .findFirst()
          .orElse(null);
    }
  }
}
