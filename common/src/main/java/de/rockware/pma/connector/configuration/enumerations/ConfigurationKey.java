package de.rockware.pma.connector.configuration.enumerations;

public enum ConfigurationKey {
  MAIL_USERNAME(
      "pma.connector@pma-connector.com",
      ConfigurationValueType.STRING,
      ConfigurationGroup.MAIL,
      false),
  MAIL_PASSWORD("pma-connector-pwd", ConfigurationValueType.STRING, ConfigurationGroup.MAIL, false),
  MAIL_SENDER(
      "pma.connector@pma-connector.com",
      ConfigurationValueType.STRING,
      ConfigurationGroup.MAIL,
      false),
  MAIL_SMTP_HOST(
      "smtp.pma-connector.com", ConfigurationValueType.STRING, ConfigurationGroup.MAIL, false),
  MAIL_SMTP_PORT("465", ConfigurationValueType.INTEGER, ConfigurationGroup.MAIL, false),
  MAIL_SMTP_TIMEOUT("5000", ConfigurationValueType.INTEGER, ConfigurationGroup.MAIL, false),
  MAIL_SMTP_SOCKET_FACTORY_PORT(
      "465", ConfigurationValueType.INTEGER, ConfigurationGroup.MAIL, false),
  MAIL_SMTP_SOCKET_FACTORY_CLASS(
      "javax.net.ssl.SSLSocketFactory",
      ConfigurationValueType.STRING,
      ConfigurationGroup.MAIL,
      false),
  MAIL_TO_RECIPIENTS(
      "recipient@pma-connector.de", ConfigurationValueType.STRING, ConfigurationGroup.MAIL, true),
  MAIL_CC_RECIPIENTS(
      "ccrecipient@pma-connector.de", ConfigurationValueType.STRING, ConfigurationGroup.MAIL, true),
  MAIL_BCC_RECIPIENTS("", ConfigurationValueType.STRING, ConfigurationGroup.MAIL, true),
  MAIL_SENDING_ENABLED("false", ConfigurationValueType.BOOLEAN, ConfigurationGroup.MAIL, false),
  MAIL_SENDING_ATTACH_LOGS("false", ConfigurationValueType.BOOLEAN, ConfigurationGroup.MAIL, false),
  DELETE_IMPORTED_CONFIGURATION_FILE(
      "true", ConfigurationValueType.BOOLEAN, ConfigurationGroup.CONFIGURATION, false),
  DELETE_IMPORTED_MAPPING_FILE(
      "true", ConfigurationValueType.BOOLEAN, ConfigurationGroup.CONFIGURATION, false),
  FILENAME_SEPARATOR("_", ConfigurationValueType.STRING, ConfigurationGroup.FILENAME, false),
  FILENAME_CAMPAIGN_ID_INDEX(
      "0", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_CAMPAIGN_NAME_INDEX(
      "1", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_DELIVERY_ID_INDEX(
      "2", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_DELIVERY_NAME_INDEX(
      "3", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_CAMPAIGN_START_DATE_INDEX(
      "4", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_CAMPAIGN_END_DATE_INDEX(
      "5", ConfigurationValueType.INTEGER, ConfigurationGroup.FILENAME, false),
  FILENAME_DATE_FORMAT(
      "yyyy-MM-dd", ConfigurationValueType.STRING, ConfigurationGroup.FILENAME, false),
  EXTRACTION_TYPE("INTERNAL", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION, false),
  EXTRACTION_SFTP_HOST(
      "host", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_PORT(
      "22", ConfigurationValueType.INTEGER, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_USERNAME(
      "sftp_user", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_PASSWORD(
      "sftp_pwd", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_SESSION_TIMEOUT(
      "10000", ConfigurationValueType.INTEGER, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_CHANNEL_TIMEOUT(
      "5000", ConfigurationValueType.INTEGER, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_SFTP_PATH(
      "Resources", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_SFTP, true),
  EXTRACTION_FTP_HOST(
      "host", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_FTP, true),
  EXTRACTION_FTP_PORT(
      "10000", ConfigurationValueType.INTEGER, ConfigurationGroup.EXTRACTION_FTP, true),
  EXTRACTION_FTP_USERNAME(
      "ftp_user", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_FTP, true),
  EXTRACTION_FTP_PASSWORD(
      "ftppwd1234", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_FTP, true),
  EXTRACTION_FTP_PATH(
      "Resources", ConfigurationValueType.STRING, ConfigurationGroup.EXTRACTION_FTP, true),
  EXTRACTION_FTP_ACTIVE_MODE(
      "false", ConfigurationValueType.BOOLEAN, ConfigurationGroup.EXTRACTION_FTP, true),
  TRANSFORMATION_TYPE(
      "MAPPING", ConfigurationValueType.STRING, ConfigurationGroup.TRANSFORMATION, false),
  LOAD_TYPE("PMA_REST", ConfigurationValueType.STRING, ConfigurationGroup.LOAD, false),
  PMA_PARTNER_SYSTEM_ID(
      "pma.connector.ps.id", ConfigurationValueType.STRING, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_PARTNER_SYSTEM_CUSTOMER_ID(
      "pma.connector.ps.cus.id",
      ConfigurationValueType.STRING,
      ConfigurationGroup.LOAD_PMA_REST,
      true),
  PMA_AUTHENTICATION_SECRET(
      "$?Pm4-C0nn3ct0r-pwd?$",
      ConfigurationValueType.STRING,
      ConfigurationGroup.LOAD_PMA_REST,
      true),
  PMA_AUTHENTICATION_LOCALE(
      "en", ConfigurationValueType.STRING, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_SERVICE_BASE_URL(
      "http://host:8080", ConfigurationValueType.STRING, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_SERVICE_TIMEOUT(
      "5000", ConfigurationValueType.INTEGER, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_SERVICE_AUTHENTICATION_URL_FRAGMENT(
      "user/authentication/partnersystem/credentialsbased",
      ConfigurationValueType.STRING,
      ConfigurationGroup.LOAD_PMA_REST,
      true),
  PMA_SERVICE_CREATE_CAMPAIGN_URL_FRAGMENT(
      "automation/longtermcampaigns",
      ConfigurationValueType.STRING,
      ConfigurationGroup.LOAD_PMA_REST,
      true),
  PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT(
      "automation/mailings", ConfigurationValueType.STRING, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT(
      "variabledefinitions", ConfigurationValueType.STRING, ConfigurationGroup.LOAD_PMA_REST, true),
  PMA_SERVICE_TRANSFER_RECIPIENTS_URL_FRAGMENT(
      "automation/recipients",
      ConfigurationValueType.STRING,
      ConfigurationGroup.LOAD_PMA_REST,
      true),
  PMA_REDIRECT_FRONTEND_BASE_URL(
      "https://uat.print-mailing-test.deutschepost.de",
      ConfigurationValueType.STRING,
      ConfigurationGroup.PMA_REDIRECT,
      false),
  PMA_REDIRECT_FRONTEND_FRAGMENT(
      "planen", ConfigurationValueType.STRING, ConfigurationGroup.PMA_REDIRECT, false),
  PMA_REDIRECT_FRONTEND_SECRET_PARAMETER_NAME(
      "partnersystem", ConfigurationValueType.STRING, ConfigurationGroup.PMA_REDIRECT, false),
  PMA_REDIRECT_SECRET(
      "72psYzEjTWXjW$J0ik=jCmcDI83B9bHGn#Y_U$_$Y#w5oO6nHvFymj5bxw$QtbZ30A56BY0gRM-27vgN_#cCbui0Y0LZTaKIvLTgS#-mh",
      ConfigurationValueType.STRING,
      ConfigurationGroup.PMA_REDIRECT,
      false),
  PMA_REDIRECT_USERNAME(
      "AdobeCampaignReply", ConfigurationValueType.STRING, ConfigurationGroup.PMA_REDIRECT, false),
  PMA_REDIRECT_FIRSTNAME(
      "Pmauser", ConfigurationValueType.STRING, ConfigurationGroup.PMA_REDIRECT, false),
  PMA_REDIRECT_LASTNAME(
      "AdobeCampaign", ConfigurationValueType.STRING, ConfigurationGroup.PMA_REDIRECT, false),
  PMA_REDIRECT_EMAIL(
      "pmaconnector@pma-connector.com",
      ConfigurationValueType.STRING,
      ConfigurationGroup.PMA_REDIRECT,
      false),
  PMA_REDIRECT_ISS(
      "com.dpdhl.dialogmarketing",
      ConfigurationValueType.STRING,
      ConfigurationGroup.PMA_REDIRECT,
      false),
  ERROR_KEEP_FILES(
      "true", ConfigurationValueType.BOOLEAN, ConfigurationGroup.ERROR_HANDLING, false),
  ERROR_WRITE_DETAILS(
      "true", ConfigurationValueType.BOOLEAN, ConfigurationGroup.ERROR_HANDLING, false),
  REST_AUTH_USERNAME(
      "pmaconnector", ConfigurationValueType.STRING, ConfigurationGroup.SECURITY, false),
  REST_AUTH_PASSWORD(
      "pmaconnectorpwd", ConfigurationValueType.STRING, ConfigurationGroup.SECURITY, false),
  OPTIONAL_MAPPINGS_TYPE("10", ConfigurationValueType.INTEGER, ConfigurationGroup.MAPPINGS, false),
  PRODUCTION_MODE("true", ConfigurationValueType.BOOLEAN, ConfigurationGroup.MODE, false);

  private final String defaultValue;
  private final ConfigurationValueType valueType;
  private final ConfigurationGroup group;
  private final boolean optional;

  ConfigurationKey(
      String defaultValue,
      ConfigurationValueType valueType,
      ConfigurationGroup group,
      boolean optional) {
    this.defaultValue = defaultValue;
    this.valueType = valueType;
    this.group = group;
    this.optional = optional;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public ConfigurationValueType getValueType() {
    return valueType;
  }

  public ConfigurationGroup getGroup() {
    return group;
  }

  public boolean isOptional() {
    return optional;
  }

  public enum ConfigurationValueType {
    STRING,
    INTEGER,
    BOOLEAN
  }

  public enum ConfigurationGroup {
    MAIL,
    CONFIGURATION,
    FILENAME,
    EXTRACTION,
    EXTRACTION_SFTP,
    EXTRACTION_FTP,
    TRANSFORMATION,
    LOAD,
    LOAD_PMA_REST,
    ERROR_HANDLING,
    SECURITY,
    PMA_REDIRECT,
    MAPPINGS,
    MODE
  }
}
