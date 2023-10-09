package de.rockware.pma.connector.common.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class StringConverter {

  public static int toInt(String value) throws NumberFormatException {
    if (StringUtils.isEmpty(value)) {
      return 0;
    }
    return Integer.parseInt(value);
  }

  public static boolean toBoolean(String value) {
    return Boolean.parseBoolean(value);
  }

  public static Date toDate(String value, String format) throws ParseException {
    return new SimpleDateFormat(format).parse(value);
  }
}
