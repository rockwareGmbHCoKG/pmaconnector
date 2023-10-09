package de.rockware.pma.connector.extraction.extractors.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createExecutionContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.channels.ChannelAdapter.ResourceNameFilter;
import de.rockware.pma.connector.channels.internal.SftpChannelAdapter;
import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.extraction.beans.ChannelResource;
import de.rockware.pma.connector.extraction.exceptions.ExtractorException;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SftpExtractorTest {
  private static final String RESOURCE_NAME =
      "CAMPAIGN1_Campaign 1_DELIVERY1_Delivery 1_2021-09-01_2021-12-31.csv";
  private static final String CAMPAIGN_ID = "CAMPAIGN1";
  private static final String CAMPAIGN_NAME = "Campaign 1";
  private static final String DELIVERY_ID = "DELIVERY1";
  private static final String DELIVERY_NAME = "Delivery 1";
  private static final Date START_DATE =
      Date.from(LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  private static final Date END_DATE =
      Date.from(
          LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

  private SftpExtractor sut;

  @Mock ChannelAdapter channelAdapter;

  @BeforeEach
  void setUp() {
    this.sut = new SftpExtractor(channelAdapter);
  }

  @Test
  void exceptionThrown() {
    doThrow(new RuntimeException("Test exception"))
        .when(channelAdapter)
        .retrieve(
            anyCollection(),
            any(ResourceNameFilter.class),
            any(SftpChannelAdapter.ResourceCallback.class));
    ExecutionContext executionContext = createExecutionContext();
    assertThrows(ExtractorException.class, () -> sut.extract(executionContext));
  }

  @Test
  void nullExecutionContext() {
    assertThrows(ExtractorException.class, () -> sut.extract(null));
  }

  @Test
  void nullResource() {
    doAnswer(
            i -> {
              SftpChannelAdapter.ResourceCallback callback =
                  i.getArgument(2, SftpChannelAdapter.ResourceCallback.class);
              callback.onRetrieval(null);
              return null;
            })
        .when(channelAdapter)
        .retrieve(
            anyCollection(),
            any(ResourceNameFilter.class),
            any(SftpChannelAdapter.ResourceCallback.class));
    ExecutionContext executionContext = createExecutionContext();
    sut.extract(executionContext);
    assertEquals(0, executionContext.getExtractedData().getRecords().size());
  }

  @Test
  void notExistingFields() {
    doAnswer(
            i -> {
              SftpChannelAdapter.ResourceCallback callback =
                  i.getArgument(2, SftpChannelAdapter.ResourceCallback.class);
              callback.onRetrieval(
                  new ChannelResource(
                      "file.csv", getResourceInputStream("resource_without_all_fields.csv")));
              return null;
            })
        .when(channelAdapter)
        .retrieve(
            anyCollection(),
            any(ResourceNameFilter.class),
            any(SftpChannelAdapter.ResourceCallback.class));
    ExecutionContext executionContext = createExecutionContext();
    sut.extract(executionContext);
    assertEquals(0, executionContext.getExtractedData().getRecords().size());
    assertEquals(1, executionContext.getExtractedData().getErrors().size());
  }

  @Test
  void invalidResourceName() {
    doAnswer(
            i -> {
              SftpChannelAdapter.ResourceCallback callback =
                  i.getArgument(2, SftpChannelAdapter.ResourceCallback.class);
              callback.onRetrieval(
                  new ChannelResource(
                      "invalid-resource-name.csv", getResourceInputStream("resource_ok.csv")));
              return null;
            })
        .when(channelAdapter)
        .retrieve(
            anyCollection(),
            any(ResourceNameFilter.class),
            any(SftpChannelAdapter.ResourceCallback.class));
    ExecutionContext executionContext = createExecutionContext();
    sut.extract(executionContext);
    ExecutionContext expectedExecutionContext = createExecutionContext();
    DataUpdater.addError(
        expectedExecutionContext,
        Constants.UNKNOWN,
        Step.EXTRACTION,
        "Error: campaign info not parsed from resource name invalid-resource-name.csv",
        null);
    assertEquals(expectedExecutionContext.getExtractedData(), executionContext.getExtractedData());
  }

  @Test
  void success() {
    doAnswer(
            i -> {
              SftpChannelAdapter.ResourceCallback callback =
                  i.getArgument(2, SftpChannelAdapter.ResourceCallback.class);
              callback.onRetrieval(
                  new ChannelResource(RESOURCE_NAME, getResourceInputStream("resource_ok.csv")));
              return null;
            })
        .when(channelAdapter)
        .retrieve(
            anyCollection(),
            any(ResourceNameFilter.class),
            any(SftpChannelAdapter.ResourceCallback.class));
    ExecutionContext executionContext = createExecutionContext();
    sut.extract(executionContext);
    ExecutionContext expectedExecutionContext = createExecutionContext();
    IntStream.rangeClosed(1, 10).forEach(i -> addRecord(expectedExecutionContext, i));
    assertEquals(expectedExecutionContext.getExtractedData(), executionContext.getExtractedData());
  }

  private InputStream getResourceInputStream(String resourceName) {
    return SftpExtractorTest.class.getClassLoader().getResourceAsStream(resourceName);
  }

  private void addRecord(ExecutionContext executionContext, int index) {
    Info info = new Info();
    info.setResourceName(RESOURCE_NAME);
    info.setCampaignId(CAMPAIGN_ID);
    info.setCampaignName(CAMPAIGN_NAME);
    info.setDeliveryId(DELIVERY_ID);
    info.setDeliveryName(DELIVERY_NAME);
    info.setStartDate(START_DATE);
    info.setEndDate(END_DATE);
    Record record = new Record(info);
    for (Mapping mapping : executionContext.getMappings()) {
      record
          .getValues()
          .add(
              new Value(mapping.getSource(), String.format("Value %d %d", mapping.getId(), index)));
    }
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, record);
  }
}
