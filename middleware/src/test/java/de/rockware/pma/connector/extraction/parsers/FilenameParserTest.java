package de.rockware.pma.connector.extraction.parsers;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.Info;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

class FilenameParserTest {

  public static final String FILENAME =
      "CampaignId1_CampaignName1_DeliveryId1_DeliveryName1_2021-09-01_2021-12-31.csv";

  @Test
  void nullConfigurationValues() {
    assertNull(FilenameParser.parse(null, FILENAME));
  }

  @Test
  void emptyConfigurationValues() {
    assertNull(FilenameParser.parse(Collections.emptyList(), FILENAME));
  }

  @Test
  void nullFilename() {
    assertNull(FilenameParser.parse(createConfigurationValues(), null));
  }

  @Test
  void emptyFilename() {
    assertNull(FilenameParser.parse(createConfigurationValues(), ""));
  }

  @Test
  void noSeparator() {
    List<ConfigurationValue> configurationValues =
        createConfigurationValues().stream()
            .filter(v -> !ConfigurationKey.FILENAME_SEPARATOR.equals(v.getKey()))
            .collect(Collectors.toList());
    assertNull(FilenameParser.parse(configurationValues, FILENAME));
  }

  @Test
  void differentSeparator() {
    assertNull(
        FilenameParser.parse(
            createConfigurationValues(),
            "CampaignId1*CampaignName1*DeliveryId1*DeliveryName1*2021-09-01*2021-12-31.csv"));
  }

  @Test
  void missingFilenamePart() {
    assertNull(
        FilenameParser.parse(
            createConfigurationValues(), "CampaignId1_DeliveryName1_2021-09-01_2021-12-31.csv"));
  }

  @Test
  void dateFormatError() {
    List<ConfigurationValue> configurationValues =
        createConfigurationValues().stream()
            .map(
                v ->
                    new ConfigurationValue(
                        v.getKey(),
                        ConfigurationKey.FILENAME_DATE_FORMAT.equals(v.getKey())
                            ? "INVALID_DATE_FORMAT"
                            : v.getValue()))
            .collect(Collectors.toList());
    assertNull(FilenameParser.parse(configurationValues, FILENAME));
  }

  @Test
  void invalidDateFormat() {
    List<ConfigurationValue> configurationValues =
        createConfigurationValues().stream()
            .map(
                v ->
                    new ConfigurationValue(
                        v.getKey(),
                        ConfigurationKey.FILENAME_DATE_FORMAT.equals(v.getKey())
                            ? "dd/MM/yyyy"
                            : v.getValue()))
            .collect(Collectors.toList());
    assertNull(FilenameParser.parse(configurationValues, FILENAME));
  }

  @Test
  void successWithoutFilenameSuffix() {
    Info expected = new Info();
    expected.setResourceName(
        "CampaignId1_CampaignName1_DeliveryId1_DeliveryName1_2021-09-01_2021-12-31");
    expected.setCampaignId("CampaignId1");
    expected.setCampaignName("CampaignName1");
    expected.setDeliveryId("DeliveryId1");
    expected.setDeliveryName("DeliveryName1");
    expected.setStartDate(
        Date.from(
            LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    expected.setEndDate(
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    assertEquals(
        expected,
        FilenameParser.parse(createConfigurationValues(), StringUtils.removeEnd(FILENAME, ".csv")));
  }

  @Test
  void success() {
    Info expected = new Info();
    expected.setResourceName(
        "CampaignId1_CampaignName1_DeliveryId1_DeliveryName1_2021-09-01_2021-12-31.csv");
    expected.setCampaignId("CampaignId1");
    expected.setCampaignName("CampaignName1");
    expected.setDeliveryId("DeliveryId1");
    expected.setDeliveryName("DeliveryName1");
    expected.setStartDate(
        Date.from(
            LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    expected.setEndDate(
        Date.from(
            LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    assertEquals(expected, FilenameParser.parse(createConfigurationValues(), FILENAME));
  }
}
