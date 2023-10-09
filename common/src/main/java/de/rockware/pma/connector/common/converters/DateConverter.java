package de.rockware.pma.connector.common.converters;

import java.util.Date;
import java.util.Objects;

public class DateConverter {

  public static Long toLong(Date date) {
    if (Objects.isNull(date)) {
      return null;
    }
    return date.getTime();
  }

  public static Date toDate(Long time) {
    if (Objects.isNull(time)) {
      return null;
    }
    return new Date(time);
  }
}
