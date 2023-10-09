package de.rockware.pma.connector.common.utils;

import org.apache.commons.lang.RandomStringUtils;

public class OidGenerator {

  public static String generate() {
    return RandomStringUtils.randomAlphanumeric(15).toUpperCase();
  }
}
