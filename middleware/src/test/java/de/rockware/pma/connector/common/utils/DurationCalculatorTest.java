package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DurationCalculatorTest {

  @Test
  void nullTimes() {
    assertEquals(0L, DurationCalculator.calculate(null, null));
  }

  @Test
  void nullEnd() {
    Date start =
        Date.from(
            LocalDateTime.of(2021, 10, 25, 16, 9, 38).atZone(ZoneId.systemDefault()).toInstant());
    assertEquals(0L, DurationCalculator.calculate(start, null));
  }

  @Test
  void nullStart() {
    Date end =
        Date.from(
            LocalDateTime.of(2021, 10, 25, 16, 9, 44).atZone(ZoneId.systemDefault()).toInstant());
    assertEquals(0L, DurationCalculator.calculate(null, end));
  }

  @Test
  void success() {
    Date start =
        Date.from(
            LocalDateTime.of(2021, 10, 25, 16, 9, 38).atZone(ZoneId.systemDefault()).toInstant());
    Date end =
        Date.from(
            LocalDateTime.of(2021, 10, 25, 16, 9, 44).atZone(ZoneId.systemDefault()).toInstant());
    assertEquals(6000L, DurationCalculator.calculate(start, end));
  }
}
