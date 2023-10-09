package de.rockware.pma.connector.load.loaders.stages.status;

import de.rockware.pma.connector.common.beans.CampaignCreationResult;
import de.rockware.pma.connector.execution.beans.Info;
import java.util.Objects;

public class PmaStatusChecker {

  public static boolean isProof(Info info) {
    return Objects.nonNull(info) && info.isProof();
  }

  public static boolean isRecurring(Info info) {
    return Objects.nonNull(info) && !isProof(info) && info.isRecurring();
  }

  public static boolean isCampaignExisting(CampaignCreationResult campaignCreationResult) {
    return Objects.nonNull(campaignCreationResult);
  }

  public static boolean isCampaignActive(Info info) {
    return Objects.nonNull(info) && info.isCampaignStarted();
  }

  public static boolean isCampaignEditable(Info info) {
    return Objects.nonNull(info) && info.isCampaignEditable();
  }
}
