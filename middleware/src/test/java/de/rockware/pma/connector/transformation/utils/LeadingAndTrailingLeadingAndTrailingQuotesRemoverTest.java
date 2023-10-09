package de.rockware.pma.connector.transformation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LeadingAndTrailingLeadingAndTrailingQuotesRemoverTest {

  @Test
  void nullValue() {
    assertNull(LeadingAndTrailingQuotesRemover.remove(null));
  }

  @Test
  void emptyValue() {
    String value = "";
    assertEquals(value, LeadingAndTrailingQuotesRemover.remove(value));
  }

  @Test
  void noLeadingAndTrailingQuotes() {
    String value = "Generic value without leading and trailing quotes";
    assertEquals(value, LeadingAndTrailingQuotesRemover.remove(value));
  }

  @Test
  void internalQuotes() {
    String value = "Generic value with \"internal\" quotes";
    assertEquals(value, LeadingAndTrailingQuotesRemover.remove(value));
  }

  @Test
  void success() {
    String value = "\"Successfully removed quotes ignoring \"internal\" ones\"";
    String expected = "Successfully removed quotes ignoring \"internal\" ones";
    assertEquals(expected, LeadingAndTrailingQuotesRemover.remove(value));
  }
}
