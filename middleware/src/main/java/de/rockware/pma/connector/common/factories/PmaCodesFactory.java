package de.rockware.pma.connector.common.factories;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.utils.NamesMerger;

public class PmaCodesFactory {

  public static String getCampaignId(String campaignId, String deliveryId) {
    return NamesMerger.concatenate(
        Constants.CAMPAIGN_AND_DELIVERY_ID_MAX_SIZE,
        campaignId,
        Constants.CAMPAIGN_ID_MAX_SIZE,
        deliveryId,
        Constants.DELIVERY_ID_MAX_SIZE,
        "_",
        Constants.UNKNOWN_VALUE);
  }

  public static String getCampaignName(String campaignName, String deliveryName) {
    return NamesMerger.concatenate(
        Constants.CAMPAIGN_AND_DELIVERY_NAME_MAX_SIZE,
        campaignName,
        Constants.CAMPAIGN_NAME_MAX_SIZE,
        deliveryName,
        Constants.DELIVERY_NAME_MAX_SIZE,
        " - ",
        Constants.UNKNOWN_VALUE);
  }
}
