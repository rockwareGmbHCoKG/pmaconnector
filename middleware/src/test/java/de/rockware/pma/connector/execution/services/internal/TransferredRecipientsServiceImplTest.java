package de.rockware.pma.connector.execution.services.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import de.rockware.pma.connector.execution.repositiories.TransferredRecipientsRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferredRecipientsServiceImplTest {
  private static final String CAMPAIGN_ID = "CAMPAIGN1";
  private static final String DELIVERY_ID = "DELIVERY1";

  @Mock private TransferredRecipientsRepository transferredRecipientsRepository;
  private TransferredRecipientsServiceImpl sut;

  @BeforeEach
  void setUp() {
    this.sut = new TransferredRecipientsServiceImpl(transferredRecipientsRepository);
  }

  @Test
  void getNullId() {
    assertNull(sut.get(null));
    verifyNoInteractions(transferredRecipientsRepository);
  }

  @Test
  void getEmptyId() {
    assertNull(sut.get(""));
    verifyNoInteractions(transferredRecipientsRepository);
  }

  @Test
  void getSuccess() {
    TransferredRecipients expected =
        new TransferredRecipients(
            "TEST_OID_1",
            "TEST_PARENT_OID_1",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID1",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            42);
    when(transferredRecipientsRepository.findById(eq("TEST_OID_1")))
        .thenReturn(Optional.of(expected));
    assertEquals(expected, sut.get("TEST_OID_1"));
    verify(transferredRecipientsRepository).findById(eq("TEST_OID_1"));
  }

  @Test
  void saveNullRecord() {
    sut.save(null);
    verifyNoInteractions(transferredRecipientsRepository);
  }

  @Test
  void saveAlreadyPresent() {
    List<TransferredRecipients> transferredRecipients = createTransferredRecipients();
    when(transferredRecipientsRepository.findByCampaignIdAndDeliveryIdAndCorrelationId(
            anyString(), anyString(), anyString()))
        .thenReturn(transferredRecipients);
    TransferredRecipients alreadyOnDb =
        new TransferredRecipients(
            "TEST_OID_4",
            "TEST_PARENT_OID_4",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID1",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            42);
    sut.save(alreadyOnDb);
    verify(transferredRecipientsRepository)
        .findByCampaignIdAndDeliveryIdAndCorrelationId(
            eq(CAMPAIGN_ID), eq(DELIVERY_ID), eq("CORRELATION_ID1"));
    verify(transferredRecipientsRepository, times(0)).save(eq(alreadyOnDb));
  }

  @Test
  void saveNullRecordsOnDb() {
    when(transferredRecipientsRepository.findByCampaignIdAndDeliveryIdAndCorrelationId(
            anyString(), anyString(), anyString()))
        .thenReturn(null);
    TransferredRecipients newTransferredRecipients =
        new TransferredRecipients(
            "TEST_OID_4",
            "TEST_PARENT_OID_4",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID4",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            42);
    sut.save(newTransferredRecipients);
    verify(transferredRecipientsRepository)
        .findByCampaignIdAndDeliveryIdAndCorrelationId(
            eq(CAMPAIGN_ID), eq(DELIVERY_ID), eq("CORRELATION_ID4"));
    verify(transferredRecipientsRepository).save(eq(newTransferredRecipients));
  }

  @Test
  void saveNoRecordsOnDb() {
    TransferredRecipients newTransferredRecipients =
        new TransferredRecipients(
            "TEST_OID_4",
            "TEST_PARENT_OID_4",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID4",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            42);
    sut.save(newTransferredRecipients);
    verify(transferredRecipientsRepository)
        .findByCampaignIdAndDeliveryIdAndCorrelationId(
            eq(CAMPAIGN_ID), eq(DELIVERY_ID), eq("CORRELATION_ID4"));
    verify(transferredRecipientsRepository).save(eq(newTransferredRecipients));
  }

  @Test
  void deleteNullId() {
    sut.delete(null);
    verifyNoInteractions(transferredRecipientsRepository);
  }

  @Test
  void deleteEmptyId() {
    sut.delete("");
    verifyNoInteractions(transferredRecipientsRepository);
  }

  @Test
  void deleteSuccess() {
    sut.delete("TEST_OID_1");
    verify(transferredRecipientsRepository).deleteById(eq("TEST_OID_1"));
  }

  @Test
  void clearSuccess() {
    sut.clear();
    verify(transferredRecipientsRepository).deleteAll();
  }

  private List<TransferredRecipients> createTransferredRecipients() {
    return Arrays.asList(
        new TransferredRecipients(
            "TEST_OID_1",
            "TEST_PARENT_OID_1",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID1",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 44)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            42),
        new TransferredRecipients(
            "TEST_OID_2",
            "TEST_PARENT_OID_2",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID2",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 46)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            12),
        new TransferredRecipients(
            "TEST_OID_3",
            "TEST_PARENT_OID_3",
            CAMPAIGN_ID,
            DELIVERY_ID,
            "CORRELATION_ID3",
            Date.from(
                LocalDateTime.of(2021, 9, 1, 12, 34, 48)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()),
            0));
  }
}
