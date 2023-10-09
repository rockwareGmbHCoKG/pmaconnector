package de.rockware.pma.connector.common.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DateConverterTest {

  @Test
  void toLongNullDate() {
    assertNull(DateConverter.toLong(null));
  }

  @Test
  void toLongSuccess() {
    assertEquals(
        1696370400000L,
        DateConverter.toLong(
            Date.from(
                LocalDate.of(2023, 10, 4).atStartOfDay(ZoneId.of("Europe/Rome")).toInstant())));
  }

  @Test
  void toDateNullLong() {
    assertNull(DateConverter.toDate(null));
  }

  @Test
  void toDateSuccess() {
    assertEquals(
        Date.from(LocalDate.of(2023, 10, 4).atStartOfDay(ZoneId.of("Europe/Rome")).toInstant()),
        DateConverter.toDate(1696370400000L));
  }
}
