package de.rockware.pma.connector.common.converters;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class ExceptionConverter {

  public static String convert(Throwable exception) {
    if (Objects.isNull(exception)) {
      return null;
    }
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    exception.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
