package de.rockware.pma.connector.execution.services.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createExecutionContext;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.channels.ChannelAdapter;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.execution.exceptions.CleanupServiceException;
import de.rockware.pma.connector.execution.services.ExecutionStatusDetailsService;
import de.rockware.pma.connector.execution.services.ExecutionStatusErrorService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
class ExecutionCompletedServiceImplTest {
  private static final String RESOURCE_NAME_1 =
      "CAMPAIGN1_Campaign 1_DELIVERY1_Delivery 1_2021-09-01_2021-12-31.csv";
  private static final String RESOURCE_NAME_2 =
      "CAMPAIGN2_Campaign 2_DELIVERY2_Delivery 2_2021-09-01_2021-12-31.csv";

  @Mock private Factory<ChannelAdapter, Collection<ConfigurationValue>> channelAdapterFactory;
  @Mock private ChannelAdapter channelAdapter;
  @Mock private ExecutionStatusErrorService executionStatusErrorService;
  @Mock private ExecutionStatusDetailsService executionStatusDetailsService;

  private ExecutionCompletedServiceImpl sut;

  @BeforeEach
  void setUp() {
    when(channelAdapterFactory.create(any())).thenReturn(channelAdapter);
    this.sut =
        new ExecutionCompletedServiceImpl(
            channelAdapterFactory, executionStatusErrorService, executionStatusDetailsService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void nullExecutionContext() {
    assertThrows(CleanupServiceException.class, () -> sut.process(null));
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void nullValuesInExecutionContext() {
    assertThrows(
        CleanupServiceException.class, () -> sut.process(new ExecutionContext(null, null, null)));
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void emptyValuesInExecutionContext() {
    assertThrows(
        CleanupServiceException.class,
        () ->
            sut.process(
                new ExecutionContext(Collections.emptyList(), Collections.emptyList(), null)));
  }

  @Test
  void exceptionThrownByChannelAdapter() {
    ExecutionContext executionContext = createValidExecutionContext();
    doThrow(new RuntimeException("Test exception!")).when(channelAdapter).delete(any(), any());
    assertThrows(CleanupServiceException.class, () -> sut.process(executionContext));
  }

  @Test
  void successWithErrors() {
    ExecutionContext executionContext = createValidExecutionContext();
    Info info = new Info();
    info.setResourceName(RESOURCE_NAME_1);
    info.setCampaignId("CAMPAIGN1");
    info.setDeliveryId("DELIVERY1");
    Record record = new Record(info);
    record.getValues().add(new Value("Field 1", "Value 1"));
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, record);
    DataUpdater.addRecord(executionContext, Step.TRANSFORMATION, record);
    DataUpdater.addRecord(executionContext, Step.LOAD, record);
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.EXTRACTION,
        "Generic extraction error",
        new RuntimeException("Generic extraction error exception!"));
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.TRANSFORMATION,
        "Generic transformation error",
        new RuntimeException("Generic transformation exception"));
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.LOAD,
        "Generic load error",
        new RuntimeException("Generic load exception"));
    sut.process(executionContext);
    verify(channelAdapter)
        .rename(
            any(),
            eq(RESOURCE_NAME_1),
            argThat(n -> n.startsWith(RESOURCE_NAME_1) && n.endsWith(".error")));
    verify(channelAdapter)
        .write(
            any(),
            argThat(n -> n.startsWith(RESOURCE_NAME_1) && n.endsWith(".error.details")),
            any());
    verify(channelAdapter).delete(any(), eq(RESOURCE_NAME_2));
    verifyNoMoreInteractions(channelAdapter);
    verify(channelAdapter, times(0)).delete(any(), eq(RESOURCE_NAME_1));
    verify(executionStatusErrorService, times(3)).save(any());
    List<String> expectedCampaignIds = Arrays.asList("CAMPAIGN1", "CAMPAIGN2");
    List<String> expectedDeliveryIds = Arrays.asList("DELIVERY1", "DELIVERY2");
    verify(executionStatusDetailsService, times(2))
        .save(
            argThat(
                a ->
                    expectedCampaignIds.contains(a.getCampaignId())
                        && expectedDeliveryIds.contains(a.getDeliveryId())));
  }

  @Test
  void successWithErrorsWithoutFileRenameAndErrorDetailsCreation() {
    ExecutionContext executionContext = createValidExecutionContext();
    executionContext.getConfigurationValues().stream()
        .filter(
            k ->
                ConfigurationKey.ERROR_KEEP_FILES.equals(k.getKey())
                    || ConfigurationKey.ERROR_WRITE_DETAILS.equals(k.getKey()))
        .forEach(v -> v.setValue("false"));
    Info info = new Info();
    info.setResourceName(RESOURCE_NAME_1);
    info.setCampaignId("CAMPAIGN1");
    info.setDeliveryId("DELIVERY1");
    Record record = new Record(info);
    record.getValues().add(new Value("Field 1", "Value 1"));
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, record);
    DataUpdater.addRecord(executionContext, Step.TRANSFORMATION, record);
    DataUpdater.addRecord(executionContext, Step.LOAD, record);
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.EXTRACTION,
        "Generic extraction error",
        new RuntimeException("Generic extraction error exception!"));
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.TRANSFORMATION,
        "Generic transformation error",
        new RuntimeException("Generic transformation exception"));
    DataUpdater.addError(
        executionContext,
        info.copy(),
        Step.LOAD,
        "Generic load error",
        new RuntimeException("Generic load exception"));
    sut.process(executionContext);
    verify(channelAdapter, times(0))
        .rename(
            any(),
            eq(RESOURCE_NAME_1),
            argThat(n -> n.startsWith(RESOURCE_NAME_1) && n.endsWith(".error")));
    verify(channelAdapter, times(0))
        .write(
            any(),
            argThat(n -> n.startsWith(RESOURCE_NAME_1) && n.endsWith(".error.details")),
            any());
    verify(channelAdapter).delete(any(), eq(RESOURCE_NAME_2));
    verifyNoMoreInteractions(channelAdapter);
    verify(channelAdapter, times(0)).delete(any(), eq(RESOURCE_NAME_1));
    verify(executionStatusErrorService, times(3)).save(any());
    List<String> expectedCampaignIds = Arrays.asList("CAMPAIGN1", "CAMPAIGN2");
    List<String> expectedDeliveryIds = Arrays.asList("DELIVERY1", "DELIVERY2");
    verify(executionStatusDetailsService, times(2))
        .save(
            argThat(
                a ->
                    expectedCampaignIds.contains(a.getCampaignId())
                        && expectedDeliveryIds.contains(a.getDeliveryId())));
  }

  @Test
  void success() {
    sut.process(createValidExecutionContext());
    List<String> expectedResourceNames = Arrays.asList(RESOURCE_NAME_1, RESOURCE_NAME_2);
    verify(channelAdapter, times(2)).delete(any(), argThat(expectedResourceNames::contains));
    verifyNoMoreInteractions(channelAdapter);
    List<String> campaignIds = Arrays.asList("CAMPAIGN1", "CAMPAIGN2");
    List<String> deliveryIds = Arrays.asList("DELIVERY1", "DELIVERY2");
    verify(executionStatusDetailsService, times(2))
        .save(
            argThat(
                a ->
                    campaignIds.contains(a.getCampaignId())
                        && deliveryIds.contains(a.getDeliveryId())));
    verifyNoInteractions(executionStatusErrorService);
  }

  private ExecutionContext createValidExecutionContext() {
    ExecutionContext executionContext = createExecutionContext();
    executionContext
        .getExtractedData()
        .getRecords()
        .add(
            new Record(
                new Info(
                    RESOURCE_NAME_1,
                    "CAMPAIGN1",
                    "Campaign 1",
                    "DELIVERY1",
                    "Delivery 1",
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    true,
                    false,
                    false,
                    null,
                    false,
                    false,
                    false,
                    null,
                    false)));
    executionContext
        .getTransformedData()
        .getRecords()
        .add(
            new Record(
                new Info(
                    RESOURCE_NAME_1,
                    "CAMPAIGN1",
                    "Campaign 1",
                    "DELIVERY1",
                    "Delivery 1",
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    true,
                    false,
                    false,
                    null,
                    false,
                    false,
                    false,
                    null,
                    false)));
    executionContext
        .getTransformedData()
        .getRecords()
        .add(
            new Record(
                new Info(
                    RESOURCE_NAME_2,
                    "CAMPAIGN2",
                    "Campaign 2",
                    "DELIVERY2",
                    "Delivery 2",
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    Date.from(
                        LocalDate.of(2021, 9, 1)
                            .atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()),
                    false,
                    false,
                    true,
                    12842,
                    false,
                    true,
                    true,
                    55671,
                    true)));
    return executionContext;
  }
}
