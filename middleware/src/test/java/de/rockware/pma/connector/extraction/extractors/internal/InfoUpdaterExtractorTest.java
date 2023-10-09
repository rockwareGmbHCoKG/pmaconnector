package de.rockware.pma.connector.extraction.extractors.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.common.utils.TestBeansFactory;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InfoUpdaterExtractorTest {

  public static final Date START_DATE =
      Date.from(
          LocalDate.of(2021, 9, 17).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  public static final Date END_DATE =
      Date.from(
          LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  public static final Date LAST_EXECUTION_TIME =
      Date.from(
          LocalDateTime.of(2021, 10, 13, 12, 16, 42).atZone(ZoneId.systemDefault()).toInstant());
  @Mock private ExecutionStatusService executionStatusService;

  private InfoUpdaterExtractor sut;

  @BeforeEach
  void setUp() {
    this.sut = new InfoUpdaterExtractor(executionStatusService);
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
  void noRecords() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, null);
    sut.extract(executionContext);
    verifyNoInteractions(executionStatusService);
  }

  @Test
  void nullInfo() {
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(null));
    sut.extract(executionContext);
    verifyNoInteractions(executionStatusService);
  }

  @Test
  void executionStatusNotFound() {
    Info info1 =
        new Info(
            "res1.csv",
            "CAMPAIGN1",
            "Campaign 1",
            "DELIVERY1",
            "Delivery 1",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    Info info2 =
        new Info(
            "res2.csv",
            "CAMPAIGN2",
            "Campaign 2",
            "DELIVERY2",
            "Delivery 2",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    Info info3 =
        new Info(
            "res3.csv",
            "CAMPAIGN3",
            "Campaign 3",
            "DELIVERY3",
            "Delivery 3",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    for (int i = 0; i < 10; i++) {
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info1.copy()));
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info2.copy()));
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info3.copy()));
    }
    sut.extract(executionContext);
    List<ExecutionStatusId> expectedIds =
        Arrays.asList(
            new ExecutionStatusId("CAMPAIGN1", "DELIVERY1"),
            new ExecutionStatusId("CAMPAIGN2", "DELIVERY2"),
            new ExecutionStatusId("CAMPAIGN3", "DELIVERY3"));
    verify(executionStatusService, times(3)).get(argThat(expectedIds::contains));
    assertTrue(
        executionContext.getExtractedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(Info::isProof));
  }

  @Test
  void executionStatusesFound() {
    when(executionStatusService.get(eq(new ExecutionStatusId("CAMPAIGN1", "DELIVERY1"))))
        .thenReturn(
            new ExecutionStatus(
                new ExecutionStatusId("CAMPAIGN1", "DELIVERY1"),
                "Campaign 1",
                START_DATE,
                END_DATE,
                "Delivery 1",
                true,
                12345,
                false,
                true,
                true,
                12346,
                true,
                LAST_EXECUTION_TIME));
    when(executionStatusService.get(eq(new ExecutionStatusId("CAMPAIGN2", "DELIVERY2"))))
        .thenReturn(
            new ExecutionStatus(
                new ExecutionStatusId("CAMPAIGN2", "DELIVERY2"),
                "Campaign 2",
                START_DATE,
                END_DATE,
                "Delivery 2",
                true,
                12347,
                false,
                true,
                true,
                12348,
                true,
                LAST_EXECUTION_TIME));
    Info info1 =
        new Info(
            "res1.csv",
            "CAMPAIGN1",
            "Campaign 1",
            "DELIVERY1",
            "Delivery 1",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    Info info2 =
        new Info(
            "res2.csv",
            "CAMPAIGN2",
            "Campaign 2",
            "DELIVERY2",
            "Delivery 2",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    Info info3 =
        new Info(
            "res3.csv",
            "CAMPAIGN3",
            "Campaign 3",
            "DELIVERY3",
            "Delivery 3",
            START_DATE,
            END_DATE,
            true,
            false,
            false,
            null,
            false,
            false,
            false,
            null,
            false);
    ExecutionContext executionContext = TestBeansFactory.createExecutionContext();
    for (int i = 0; i < 10; i++) {
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info1.copy()));
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info2.copy()));
      DataUpdater.addRecord(executionContext, Step.EXTRACTION, new Record(info3.copy()));
    }
    sut.extract(executionContext);
    List<ExecutionStatusId> expectedIds =
        Arrays.asList(
            new ExecutionStatusId("CAMPAIGN1", "DELIVERY1"),
            new ExecutionStatusId("CAMPAIGN2", "DELIVERY2"),
            new ExecutionStatusId("CAMPAIGN3", "DELIVERY3"));
    verify(executionStatusService, times(3)).get(argThat(expectedIds::contains));
    assertTrue(
        executionContext.getExtractedData().getRecords().stream()
            .map(Record::getInfo)
            .filter(
                i -> i.getCampaignId().equals("CAMPAIGN1") || i.getCampaignId().equals("CAMPAIGN2"))
            .noneMatch(Info::isProof));
    assertTrue(
        executionContext.getExtractedData().getRecords().stream()
            .map(Record::getInfo)
            .filter(i -> i.getCampaignId().equals("CAMPAIGN3"))
            .allMatch(Info::isProof));
  }
}
