package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.beans.Mailing;
import de.rockware.pma.connector.common.beans.MailingCreationResult;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.load.beans.MailingCreationResultPage;
import de.rockware.pma.connector.load.exceptions.LoadingStageException;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import java.util.Objects;

public class CreateDeliveryLoadingStage implements LoadingStage {

  private final RequestExecutor requestExecutor;
  private final ExecutionContext executionContext;
  private final String customerId;
  private final Info info;

  public CreateDeliveryLoadingStage(
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
    Mailing mailing = new Mailing(customerId, info.getCreatedCampaignId());
    MailingCreationResult mailingCreationResult =
        get(
            requestExecutor.getCreatedDeliveryPage(
                executionContext.getConfigurationValues(), mailing, info.getCreatedCampaignId()));
    if (!info.isCampaignEditable() && Objects.isNull(mailingCreationResult)) {
      throw new LoadingStageException(
          String.format(
              "Mailing does not exist for not editable campaign %s: execution aborted",
              CampaignDescriptionProvider.get(info)));
    }
    if (Objects.nonNull(mailingCreationResult)) {
      DataUpdater.addMessage(
          executionContext,
          Step.LOAD,
          info,
          String.format(
              "Mailing already existing for campaign %s, PMA mailing ID: %d",
              mailing.getCampaignId(), mailingCreationResult.getId()));
    } else {
      mailingCreationResult =
          requestExecutor.createDelivery(executionContext.getConfigurationValues(), mailing);
      if (Objects.nonNull(mailingCreationResult)) {
        DataUpdater.addMessage(
            executionContext,
            Step.LOAD,
            info,
            String.format(
                "Delivery successfully created, PMA mailing ID: %d",
                mailingCreationResult.getId()));
      }
    }
    if (Objects.nonNull(mailingCreationResult)) {
      info.setDeliveryCreated(true);
      info.setCreatedDeliveryId(mailingCreationResult.getId());
    }
  }

  private MailingCreationResult get(MailingCreationResultPage campaignCreationResultPage) {
    if (Objects.isNull(campaignCreationResultPage) || campaignCreationResultPage.isEmpty()) {
      return null;
    }
    return campaignCreationResultPage.getElements().stream()
        .filter(e -> e.getCampaignId() == info.getCreatedCampaignId())
        .findFirst()
        .orElse(null);
  }
}
