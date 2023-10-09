package de.rockware.pma.connector.common.utils;

import java.util.Date;
import java.util.Objects;

public class DurationCalculator {

  public static Long calculate(Date start, Date end) {
    if (Objects.isNull(start) || Objects.isNull(end)) {
      return 0L;
    }
    return end.getTime() - start.getTime();
  }
}
