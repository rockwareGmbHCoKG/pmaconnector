package de.rockware.pma.connector.extraction.parsers;

import de.rockware.pma.connector.common.converters.StringConverter;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.Info;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class FilenameParser {

  public static Info parse(Collection<ConfigurationValue> configurationValues, String filename) {
    String dateFormat = "undefined";
    try {
      if (StringUtils.isEmpty(filename)) {
        log.debug("Empty filename");
        return null;
      }
      String filenameWithoutExtension = StringUtils.removeEnd(filename, ".csv");
      if (Objects.isNull(configurationValues) || configurationValues.isEmpty()) {
        log.debug("Empty configuration values");
        return null;
      }
      String separator =
          ConfigurationValueRetriever.get(configurationValues, ConfigurationKey.FILENAME_SEPARATOR);
      if (StringUtils.isEmpty(separator)) {
        log.debug("No filename separator");
        return null;
      }
      int deliveryIdIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_DELIVERY_ID_INDEX);
      int deliveryNameIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_DELIVERY_NAME_INDEX);
      int campaignIdIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_CAMPAIGN_ID_INDEX);
      int campaignNameIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_CAMPAIGN_NAME_INDEX);
      int startDateIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_CAMPAIGN_START_DATE_INDEX);
      int endDateIndex =
          ConfigurationValueRetriever.getAsInt(
              configurationValues, ConfigurationKey.FILENAME_CAMPAIGN_END_DATE_INDEX);
      dateFormat =
          ConfigurationValueRetriever.get(
              configurationValues, ConfigurationKey.FILENAME_DATE_FORMAT);
      String[] fileNameParts = filenameWithoutExtension.split(separator);
      if (fileNameParts.length == 0
          || fileNameParts.length
              < Stream.of(campaignIdIndex, campaignNameIndex, startDateIndex, endDateIndex)
                      .max(Integer::compareTo)
                      .orElse(0)
                  + 1) {
        return null;
      }
      Date startDate = StringConverter.toDate(fileNameParts[startDateIndex], dateFormat);
      Date endDate = StringConverter.toDate(fileNameParts[endDateIndex], dateFormat);
      Info info = new Info();
      info.setResourceName(filename);
      info.setDeliveryId(fileNameParts[deliveryIdIndex]);
      info.setDeliveryName(fileNameParts[deliveryNameIndex]);
      info.setCampaignId(fileNameParts[campaignIdIndex]);
      info.setCampaignName(fileNameParts[campaignNameIndex]);
      info.setStartDate(startDate);
      info.setEndDate(endDate);
      return info;
    } catch (Throwable e) {
      log.debug(
          String.format("Error parsing filename %s (with date format %s)", filename, dateFormat));
      return null;
    }
  }
}
