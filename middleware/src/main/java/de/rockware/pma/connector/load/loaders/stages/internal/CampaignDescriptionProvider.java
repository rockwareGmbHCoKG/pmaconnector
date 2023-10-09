package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.execution.beans.Info;
import java.util.Objects;
import java.util.Optional;

class CampaignDescriptionProvider {

  static String get(Info info) {
    if (Objects.isNull(info)) {
      return Constants.UNKNOWN_VALUE;
    }
    String campaignId = Optional.ofNullable(info.getCampaignId()).orElse(Constants.UNKNOWN_VALUE);
    String campaignName =
        Optional.ofNullable(info.getCampaignName()).orElse(Constants.UNKNOWN_VALUE);
    StringBuilder builder = new StringBuilder(campaignId).append(" - ").append(campaignName);
    if (Objects.nonNull(info.getCreatedCampaignId())) {
      builder.append(" (").append(info.getCreatedCampaignId()).append(")");
    }
    return builder.toString();
  }
}
