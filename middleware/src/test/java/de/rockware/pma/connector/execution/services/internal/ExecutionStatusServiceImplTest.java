package de.rockware.pma.connector.execution.services.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExecutionStatusServiceImplTest {
  private static final String CAMPAIGN_ID = "CAMPAIGN1";
  private static final String DELIVERY_ID = "DELIVERY1";
  private static final String CAMPAIGN_NAME = "Campaign 1";
  private static final Date CAMPAIGN_START_DATE =
      Date.from(LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  private static final Date CAMPAIGN_END_DATE =
      Date.from(
          LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  private static final String DELIVERY_NAME = "Delivery 1";

  @Mock private ExecutionStatusRepository executionStatusRepository;
  private ExecutionStatusServiceImpl sut;

  @BeforeEach
  void setUp() {
    this.sut = new ExecutionStatusServiceImpl(executionStatusRepository);
  }

  @Test
  void getNullId() {
    assertNull(sut.get(null));
    verifyNoInteractions(executionStatusRepository);
  }

  @Test
  void getSuccess() {
    ExecutionStatusId id = new ExecutionStatusId(CAMPAIGN_ID, DELIVERY_ID);
    ExecutionStatus executionStatus =
        new ExecutionStatus(
            id,
            CAMPAIGN_NAME,
            CAMPAIGN_START_DATE,
            CAMPAIGN_END_DATE,
            DELIVERY_NAME,
            false,
            null,
            false,
            false,
            false,
            null,
            false,
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
    when(executionStatusRepository.findById(eq(id))).thenReturn(Optional.of(executionStatus));
    assertEquals(executionStatus, sut.get(id));
    verify(executionStatusRepository).findById(eq(id));
  }

  @Test
  void saveNullRecord() {
    sut.save(null);
    verifyNoInteractions(executionStatusRepository);
  }

  @Test
  void save() {
    ExecutionStatusId id = new ExecutionStatusId(CAMPAIGN_ID, DELIVERY_ID);
    ExecutionStatus executionStatus =
        new ExecutionStatus(
            id,
            CAMPAIGN_NAME,
            CAMPAIGN_START_DATE,
            CAMPAIGN_END_DATE,
            DELIVERY_NAME,
            false,
            null,
            false,
            false,
            false,
            null,
            false,
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
    sut.save(executionStatus);
    verify(executionStatusRepository).save(eq(executionStatus));
  }

  @Test
  void deleteNullId() {
    sut.delete(null);
    verifyNoInteractions(executionStatusRepository);
  }

  @Test
  void deleteSuccess() {
    ExecutionStatusId id = new ExecutionStatusId(CAMPAIGN_ID, DELIVERY_ID);
    sut.delete(id);
    verify(executionStatusRepository).deleteById(eq(id));
  }

  @Test
  void clearSuccess() {
    sut.clear();
    verify(executionStatusRepository).deleteAll();
  }
}
