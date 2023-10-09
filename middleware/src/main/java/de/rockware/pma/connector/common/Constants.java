package de.rockware.pma.connector.common;

import de.rockware.pma.connector.execution.beans.Info;

public class Constants {
  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String SHARED_DIR_FOLDER_PATH = "/shared";
  public static final int CAMPAIGN_ID_MAX_SIZE = 10;
  public static final int DELIVERY_ID_MAX_SIZE = 10;
  public static final int CAMPAIGN_AND_DELIVERY_ID_MAX_SIZE = 21;
  public static final int CAMPAIGN_NAME_MAX_SIZE = 23;
  public static final int DELIVERY_NAME_MAX_SIZE = 24;
  public static final int CAMPAIGN_AND_DELIVERY_NAME_MAX_SIZE = 50;
  public static final String UNKNOWN_VALUE = "unknown";
  public static final Info UNKNOWN =
      new Info(
          UNKNOWN_VALUE,
          UNKNOWN_VALUE,
          UNKNOWN_VALUE,
          UNKNOWN_VALUE,
          UNKNOWN_VALUE,
          null,
          null,
          true,
          false,
          false,
          null,
          false,
          false,
          false,
          null,
          false);
}
