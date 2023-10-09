package de.rockware.pma.connector.common.utils;

import java.util.Optional;
import org.apache.commons.lang.StringUtils;

public class NamesMerger {

  public static String concatenate(
      int maxSize,
      String firstPart,
      int firstPartMaxSize,
      String secondPart,
      int secondPartMaxSize,
      String separator,
      String defaultPartValue) {
    String firstPartNullSafe = Optional.ofNullable(firstPart).orElse(defaultPartValue);
    String secondPartNullSafe = Optional.ofNullable(secondPart).orElse(defaultPartValue);
    String concatenated = String.format("%s%s%s", firstPartNullSafe, separator, secondPartNullSafe);
    if (concatenated.length() < maxSize) {
      return concatenated;
    }
    return String.format(
        "%s%s%s",
        StringUtils.substring(firstPartNullSafe, 0, firstPartMaxSize),
        separator,
        StringUtils.substring(secondPartNullSafe, 0, secondPartMaxSize));
  }
}
