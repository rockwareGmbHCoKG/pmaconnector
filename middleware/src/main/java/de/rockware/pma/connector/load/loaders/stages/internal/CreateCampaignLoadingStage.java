package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.beans.Campaign;
import de.rockware.pma.connector.common.beans.CampaignCreationResult;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.PmaCodesFactory;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.common.utils.CampaignChecker;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.load.beans.CampaignCreationResultPage;
import de.rockware.pma.connector.load.exceptions.LoadingStageException;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import de.rockware.pma.connector.load.loaders.stages.status.PmaStatusChecker;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

public class CreateCampaignLoadingStage implements LoadingStage {
  private final RequestExecutor requestExecutor;
  private final ExecutionContext executionContext;
  private final String customerId;
  private final Info info;

  public CreateCampaignLoadingStage(
      RequestExecutor requestExecutor,
      ExecutionContext executionContext,
      String customerId,
      Info info) {
    this.requestExecutor = requestExecutor;
    this.executionContext = executionContext;
    this.customerId = customerId;
    this.info = info;
  }

  @Override
  public void execute() {
    Campaign campaign = new Campaign(getCampaignId(), getCampaignName(), customerId);
    CampaignCreationResult campaignCreationResult =
        get(
            requestExecutor.getCreatedCampaignsPage(
                executionContext.getConfigurationValues(), campaign),
            info.getCampaignName(),
            info.getDeliveryName());
    if (!PmaStatusChecker.isProof(info)
        && !PmaStatusChecker.isRecurring(info)
        && !PmaStatusChecker.isCampaignExisting(campaignCreationResult)) {
      throw new LoadingStageException(
          String.format(
              "Campaign %s does not exist on PMA while transferring recipients: execution aborted",
              CampaignDescriptionProvider.get(info)));
    }
    if (PmaStatusChecker.isCampaignExisting(campaignCreationResult)) {
      DataUpdater.addMessage(
          executionContext,
          Step.LOAD,
          info,
          String.format(
              "Campaign %s already created, PMA campaign ID: %d",
              CampaignDescriptionProvider.get(info), campaignCreationResult.getId()));
    } else {
      campaignCreationResult =
          requestExecutor.createCampaign(executionContext.getConfigurationValues(), campaign);
      if (PmaStatusChecker.isCampaignExisting(campaignCreationResult)) {
        DataUpdater.addMessage(
            executionContext,
            Step.LOAD,
            info,
            String.format(
                "Campaign %s successfully created, PMA campaign ID: %d",
                CampaignDescriptionProvider.get(info), campaignCreationResult.getId()));
      }
    }
    if (PmaStatusChecker.isCampaignExisting(campaignCreationResult)) {
      info.setCampaignCreated(true);
      info.setCampaignEditable(CampaignChecker.isEditable(campaignCreationResult));
      info.setCampaignStarted(CampaignChecker.isStarted(campaignCreationResult));
      info.setCreatedCampaignId(campaignCreationResult.getId());
    }
    if (PmaStatusChecker.isCampaignActive(info) && PmaStatusChecker.isProof(info)) {
      throw new LoadingStageException(
          String.format(
              "Campaign %s is already active: sending proof is not allowed",
              CampaignDescriptionProvider.get(info)));
    }
    if (!PmaStatusChecker.isCampaignActive(info) && !PmaStatusChecker.isProof(info)) {
      throw new LoadingStageException(
          String.format(
              "Campaign %s is not active: transferring recipients is not allowed",
              CampaignDescriptionProvider.get(info)));
    }
  }

  private String getCampaignId() {
    return PmaCodesFactory.getCampaignId(info.getCampaignId(), info.getDeliveryId());
  }

  private String getCampaignName() {
    return PmaCodesFactory.getCampaignName(info.getCampaignName(), info.getDeliveryName());
  }

  private CampaignCreationResult get(
      CampaignCreationResultPage campaignCreationResultPage,
      String campaignName,
      String deliveryName) {
    if (Objects.isNull(campaignCreationResultPage) || campaignCreationResultPage.isEmpty()) {
      return null;
    }
    return campaignCreationResultPage.getElements().stream()
        .filter(
            c ->
                StringUtils.isNotEmpty(c.getCampaignName())
                    && c.getCampaignName()
                        .startsWith(
                            StringUtils.substring(
                                campaignName, 0, Constants.CAMPAIGN_NAME_MAX_SIZE))
                    && c.getCampaignName()
                        .endsWith(
                            StringUtils.substring(
                                deliveryName, 0, Constants.DELIVERY_NAME_MAX_SIZE)))
        .findFirst()
        .orElse(null);
  }
}
