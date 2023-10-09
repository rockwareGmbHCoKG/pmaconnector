package de.rockware.pma.connector.extraction.extractors.internal;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.ChannelAdapter.ResourceNameFilter;
import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.NotExistingFieldsRetriever;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.extraction.beans.ChannelResource;
import de.rockware.pma.connector.extraction.exceptions.ExtractorException;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.extraction.parsers.FilenameParser;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

@Slf4j
public class ChannelExtractor implements Extractor {

  private final ChannelAdapter channelAdapter;
  private final ResourceNameFilter resourceNameFilter;
  private final InfoProvider infoProvider;

  public ChannelExtractor(
      ChannelAdapter channelAdapter,
      ResourceNameFilter resourceNameFilter,
      InfoProvider infoProvider) {
    this.channelAdapter = channelAdapter;
    this.resourceNameFilter = resourceNameFilter;
    this.infoProvider = infoProvider;
  }

  public ChannelExtractor(ChannelAdapter channelAdapter, ResourceNameFilter resourceNameFilter) {
    this(
        channelAdapter,
        resourceNameFilter,
        (c, n) -> FilenameParser.parse(c.getConfigurationValues(), n));
  }

  @Override
  public void extract(ExecutionContext executionContext) {
    try {
      channelAdapter.retrieve(
          executionContext.getConfigurationValues(),
          resourceNameFilter,
          r -> extract(r, executionContext));
      log.debug("Execution completed");
    } catch (Throwable e) {
      throw new ExtractorException(
          String.format("Error performing extraction: %s", e.getMessage()), e);
    }
  }

  private void extract(ChannelResource channelResource, ExecutionContext executionContext) {
    if (Objects.isNull(channelResource) || Objects.isNull(channelResource.getInputStream())) {
      log.debug(String.format("Invalid resource: %s", channelResource));
      return;
    }
    CSVFormat format = CSVFormat.Builder.create().setDelimiter(",").setHeader().build();
    String resourceName = channelResource.getName();
    try (BOMInputStream bomInputStream =
            new BOMInputStream(
                channelResource.getInputStream(),
                ByteOrderMark.UTF_8,
                ByteOrderMark.UTF_16BE,
                ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_32BE,
                ByteOrderMark.UTF_32LE);
        CSVParser parser = new CSVParser(new InputStreamReader(bomInputStream), format)) {
      Info info = infoProvider.getInfo(executionContext, resourceName);
      List<String> headerNames = parser.getHeaderNames();
      Collection<String> notExistingFields =
          NotExistingFieldsRetriever.get(executionContext.getMappings(), headerNames);
      if (Objects.isNull(notExistingFields)) {
        DataUpdater.addError(
            executionContext,
            info,
            Step.EXTRACTION,
            String.format(
                "Error retrieving not existing fields, mappings:%s, csv file headers:%s",
                executionContext.getMappings(), headerNames),
            null);
        return;
      }
      if (!notExistingFields.isEmpty()) {
        DataUpdater.addError(
            executionContext,
            info,
            Step.EXTRACTION,
            String.format(
                "Missing mandatory source fields: %s",
                notExistingFields.stream().sorted().collect(Collectors.toList())),
            null);
        return;
      }
      parser.stream().forEach(r -> addRecord(info, r, headerNames, executionContext, resourceName));
      log.debug(String.format("%s import executed successfully", resourceName));
    } catch (Throwable e) {
      String message =
          String.format("Error during %s processing: %s", resourceName, e.getMessage());
      log.error(message, e);
      DataUpdater.addError(executionContext, Constants.UNKNOWN, Step.EXTRACTION, message, e);
    }
  }

  private void addRecord(
      Info info,
      CSVRecord csvRecord,
      List<String> fields,
      ExecutionContext executionContext,
      String resourceName) {
    if (Objects.isNull(info)) {
      String message =
          String.format("Error: campaign info not parsed from resource name %s", resourceName);
      DataUpdater.addError(executionContext, Constants.UNKNOWN, Step.EXTRACTION, message, null);
      log.error(message);
      return;
    }
    Record record = new Record(info);
    for (String field : fields) {
      record.getValues().add(new Value(field, csvRecord.get(field)));
    }
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, record);
  }

  public interface InfoProvider {

    Info getInfo(ExecutionContext executionContext, String resourceName);
  }
}
