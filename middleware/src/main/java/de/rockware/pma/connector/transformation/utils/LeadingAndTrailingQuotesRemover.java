package de.rockware.pma.connector.transformation.utils;

import org.apache.commons.lang.StringUtils;

public class LeadingAndTrailingQuotesRemover {

  public static String remove(String value) {
    if (StringUtils.isEmpty(value)) {
      return value;
    }
    if (value.startsWith("\"")) {
      value = value.substring(1);
    }
    if (value.endsWith("\"")) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }
}
