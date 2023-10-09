package de.rockware.pma.connector.common.utils;

import de.rockware.pma.connector.common.beans.CampaignCreationResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CampaignChecker {

  private static final List<Integer> EDITABLE_STATE_IDS = Arrays.asList(110, 125);
  private static final List<Integer> ACTIVE_STATE_IDS = Collections.singletonList(120);

  public static boolean isEditable(CampaignCreationResult campaignCreationResult) {
    return check(campaignCreationResult, EDITABLE_STATE_IDS);
  }

  public static boolean isStarted(CampaignCreationResult campaignCreationResult) {
    return check(campaignCreationResult, ACTIVE_STATE_IDS);
  }

  private static boolean check(
      CampaignCreationResult campaignCreationResult, List<Integer> stateIds) {
    if (Objects.isNull(campaignCreationResult)) {
      return false;
    }
    if (Objects.nonNull(campaignCreationResult.getStateId())) {
      return stateIds.contains(campaignCreationResult.getStateId());
    }
    if (Objects.nonNull(campaignCreationResult.getCampaignState())) {
      return stateIds.contains(campaignCreationResult.getCampaignState().getId());
    }
    return false;
  }
}
