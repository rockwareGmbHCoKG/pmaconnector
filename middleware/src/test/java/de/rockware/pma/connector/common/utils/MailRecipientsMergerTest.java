package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class MailRecipientsMergerTest {

  @Test
  void everythingNull() {
    assertNull(MailRecipientsMerger.merge(null, null));
  }

  @Test
  void everythingEmpty() {
    assertNull(MailRecipientsMerger.merge("", Collections.emptyList()));
  }

  @Test
  void singleRecipient() {
    assertEquals(
        "recipient1@test.com",
        MailRecipientsMerger.merge("recipient1@test.com", Collections.emptyList()));
  }

  @Test
  void singleRecipientAndSingleAdditionalRecipient() {
    assertEquals(
        "recipient1@test.com,recipient4@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com", Collections.singletonList("recipient4@test.com")));
  }

  @Test
  void singleRecipientAndMultipleAdditionalRecipients() {
    assertEquals(
        "recipient1@test.com,recipient4@test.com,recipient5@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com",
            Collections.singletonList("recipient4@test.com,recipient5@test.com")));
  }

  @Test
  void multipleRecipient() {
    assertEquals(
        "recipient1@test.com,recipient2@test.com,recipient3@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com,recipient2@test.com,recipient3@test.com",
            Collections.emptyList()));
  }

  @Test
  void multipleRecipientsAndSingleAdditionalRecipient() {
    assertEquals(
        "recipient1@test.com,recipient2@test.com,recipient3@test.com,recipient4@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com,recipient2@test.com,recipient3@test.com",
            Collections.singletonList("recipient4@test.com")));
  }

  @Test
  void multipleRecipientsAndMultipleAdditionalRecipients() {
    assertEquals(
        "recipient1@test.com,recipient2@test.com,recipient3@test.com,recipient4@test.com,recipient5@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com,recipient2@test.com,recipient3@test.com",
            Collections.singletonList("recipient4@test.com,recipient5@test.com")));
  }

  @Test
  void multipleRecipientsAndMultipleAdditionalRecipientsWithRepetitions() {
    assertEquals(
        "recipient1@test.com,recipient2@test.com,recipient3@test.com,recipient4@test.com,recipient5@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com,recipient2@test.com,recipient3@test.com,recipient4@test.com",
            Collections.singletonList(
                "recipient3@test.com,recipient4@test.com,recipient5@test.com")));
  }

  @Test
  void multipleRecipientsAndMultipleAdditionalRecipientsWithSpaces() {
    assertEquals(
        "recipient1@test.com,recipient2@test.com,recipient3@test.com,recipient4@test.com,recipient5@test.com",
        MailRecipientsMerger.merge(
            "recipient1@test.com , recipient2@test.com,  recipient3@test.com",
            Collections.singletonList(" recipient4@test.com , recipient5@test.com ")));
  }

  @Test
  void singleAdditionalRecipient() {
    assertEquals(
        "recipient4@test.com",
        MailRecipientsMerger.merge(null, Collections.singletonList("recipient4@test.com")));
  }

  @Test
  void multipleAdditionalRecipients() {
    assertEquals(
        "recipient4@test.com,recipient5@test.com",
        MailRecipientsMerger.merge(
            null, Collections.singletonList("recipient4@test.com,recipient5@test.com")));
  }
}
