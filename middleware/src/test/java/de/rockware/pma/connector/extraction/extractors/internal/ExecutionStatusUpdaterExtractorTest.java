package de.rockware.pma.connector.extraction.extractors.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.common.utils.TestBeansFactory;
import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExecutionStatusUpdaterExtractorTest {

  @Mock private ExecutionStatusService executionStatusService;

  private ExecutionStatusUpdaterExtractor sut;

  @BeforeEach
  void setUp() {
    this.sut = new ExecutionStatusUpdaterExtractor(executionStatusService);
  }

  @Test
  void nullExecutionContext() {
    sut.extract(null);
    verifyNoInteractions(executionStatusService);
  }

  @Test
  void noExtractedData() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    sut.extract(executionContext);
    verifyNoInteractions(executionStatusService);
  }

  @Test
  void nullRecord() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, null);
    sut.extract(executionContext);
    verifyNoInteractions(executionStatusService);
  }

  @Test
  void noExecutionStatusOnDbAndOnlyErrors() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    String resourceName =
        "CampaignId_Campaign name_DeliveryId_Delivery name_2021-09-17_2021-12-31.csv";
    String campaignId = "CampaignId";
    String campaignName = "Campaign name";
    String deliveryId = "DeliveryId";
    String deliveryName = "Delivery name";
    Date startDate =
        Date.from(
            LocalDate.of(2021, 9, 17).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    Date endDate =
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    DataUpdater.addError(
        executionContext,
        new Info(
            resourceName,
            campaignId,
            campaignName,
            deliveryId,
            deliveryName,
            startDate,
            endDate,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false),
        Step.EXTRACTION,
        "Test message",
        new RuntimeException("Test exception"));
    sut.extract(executionContext);
    ExecutionStatusId id = new ExecutionStatusId(campaignId, deliveryId);
    verify(executionStatusService).get(eq(id));
    ArgumentCaptor<ExecutionStatus> executionStatusArgumentCaptor =
        ArgumentCaptor.forClass(ExecutionStatus.class);
    verify(executionStatusService).save(executionStatusArgumentCaptor.capture());
    ExecutionStatus value = executionStatusArgumentCaptor.getValue();
    assertEquals(
        new ExecutionStatus(
            id,
            campaignName,
            startDate,
            endDate,
            deliveryName,
            false,
            null,
            false,
            false,
            false,
            null,
            false,
            value.getLastExecutionTime()),
        value);
  }

  @Test
  void noExecutionStatusOnDb() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    String resourceName =
        "CampaignId_Campaign name_DeliveryId_Delivery name_2021-09-17_2021-12-31.csv";
    String campaignId = "CampaignId";
    String campaignName = "Campaign name";
    String deliveryId = "DeliveryId";
    String deliveryName = "Delivery name";
    Date startDate =
        Date.from(
            LocalDate.of(2021, 9, 17).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    Date endDate =
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    DataUpdater.addRecord(
        executionContext,
        Step.EXTRACTION,
        new Record(
            new Info(
                resourceName,
                campaignId,
                campaignName,
                deliveryId,
                deliveryName,
                startDate,
                endDate,
                true,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false)));
    DataUpdater.addError(
        executionContext,
        new Info(
            resourceName,
            campaignId,
            campaignName,
            deliveryId,
            deliveryName,
            startDate,
            endDate,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false),
        Step.EXTRACTION,
        "Test message",
        new RuntimeException("Test exception"));
    sut.extract(executionContext);
    ExecutionStatusId id = new ExecutionStatusId(campaignId, deliveryId);
    verify(executionStatusService).get(eq(id));
    ArgumentCaptor<ExecutionStatus> executionStatusArgumentCaptor =
        ArgumentCaptor.forClass(ExecutionStatus.class);
    verify(executionStatusService).save(executionStatusArgumentCaptor.capture());
    ExecutionStatus value = executionStatusArgumentCaptor.getValue();
    assertEquals(
        new ExecutionStatus(
            id,
            campaignName,
            startDate,
            endDate,
            deliveryName,
            false,
            null,
            false,
            false,
            false,
            null,
            false,
            value.getLastExecutionTime()),
        value);
  }

  @Test
  void executionStatusAlreadyOnDb() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    String resourceName =
        "CampaignId_Campaign name_DeliveryId_Delivery name_2021-09-17_2021-12-31.csv";
    String campaignId = "CampaignId";
    String campaignName = "Campaign name";
    String deliveryId = "DeliveryId";
    String deliveryName = "Delivery name";
    Date startDate =
        Date.from(
            LocalDate.of(2021, 9, 17).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    Date endDate =
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    ExecutionStatusId id = new ExecutionStatusId(campaignId, deliveryId);
    Date lastExecutionTime =
        Date.from(
            LocalDateTime.of(2021, 9, 1, 12, 34, 44).atZone(ZoneId.systemDefault()).toInstant());
    ExecutionStatus executionStatus =
        new ExecutionStatus(
            id,
            campaignName,
            startDate,
            endDate,
            deliveryName,
            true,
            84562,
            false,
            true,
            true,
            99642,
            true,
            lastExecutionTime);
    DataUpdater.addRecord(
        executionContext,
        Step.EXTRACTION,
        new Record(
            new Info(
                resourceName,
                campaignId,
                campaignName,
                deliveryId,
                deliveryName,
                startDate,
                endDate,
                true,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false)));
    when(executionStatusService.get(eq(id))).thenReturn(executionStatus);
    sut.extract(executionContext);
    verify(executionStatusService).get(eq(id));
    verify(executionStatusService)
        .save(
            argThat(
                a ->
                    a.getId().equals(id)
                        && a.getCampaignName().equals(campaignName)
                        && a.getCampaignStartDate().equals(startDate)
                        && a.getCampaignEndDate().equals(endDate)
                        && a.isCampaignCreated()
                        && a.getCreatedCampaignId().equals(84562)
                        && a.isDeliveryCreated()
                        && a.getCreatedDeliveryId().equals(99642)
                        && a.isFieldsDefinitionDone()
                        && a.getLastExecutionTime().getTime() > lastExecutionTime.getTime()));
    Data expected = new Data();
    expected
        .getRecords()
        .add(
            new Record(
                new Info(
                    resourceName,
                    campaignId,
                    campaignName,
                    deliveryId,
                    deliveryName,
                    startDate,
                    endDate,
                    true,
                    false,
                    true,
                    84562,
                    false,
                    true,
                    true,
                    99642,
                    true)));
    assertEquals(expected, DataRetriever.getData(executionContext, Step.EXTRACTION));
  }
}
