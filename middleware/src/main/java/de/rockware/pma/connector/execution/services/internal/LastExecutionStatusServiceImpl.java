package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.beans.AuthenticationBody;
import de.rockware.pma.connector.common.beans.Campaign;
import de.rockware.pma.connector.common.beans.CampaignCreationResult;
import de.rockware.pma.connector.common.beans.Token;
import de.rockware.pma.connector.common.factories.PmaCodesFactory;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.utils.CampaignChecker;
import de.rockware.pma.connector.common.utils.TokenDecoder;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.beans.LastExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusRepository;
import de.rockware.pma.connector.execution.services.LastExecutionStatusService;
import de.rockware.pma.connector.load.beans.CampaignCreationResultPage;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LastExecutionStatusServiceImpl implements LastExecutionStatusService {

  private final ExecutionStatusRepository executionStatusRepository;
  private final ConfigurationService configurationService;
  private final RequestExecutor requestExecutor;

  @Autowired
  public LastExecutionStatusServiceImpl(
      ExecutionStatusRepository executionStatusRepository,
      ConfigurationService configurationService,
      RequestExecutor requestExecutor) {
    this.executionStatusRepository = executionStatusRepository;
    this.configurationService = configurationService;
    this.requestExecutor = requestExecutor;
  }

  @Override
  public LastExecutionStatus get(String campaignId, String deliveryId) {
    LastExecutionStatus result = new LastExecutionStatus();
    result.setCampaignId(campaignId);
    result.setDeliveryId(deliveryId);
    ExecutionStatus executionStatus = executionStatusRepository.findLast(campaignId, deliveryId);
    if (Objects.isNull(executionStatus)) {
      return getFromPma(campaignId, deliveryId);
    }
    result.setExisting(true);
    result.setActive(executionStatus.isCampaignStarted());
    result.setEditable(!executionStatus.isDeliveryCreated());
    result.setRetrievedFromPma(false);
    return result;
  }

  private LastExecutionStatus getFromPma(String campaignId, String deliveryId) {
    Collection<ConfigurationValue> configurationValues = configurationService.getAll();
    AuthenticationBody authenticationBody =
        new AuthenticationBody(
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_ID),
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.PMA_PARTNER_SYSTEM_CUSTOMER_ID),
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.PMA_AUTHENTICATION_SECRET),
            ConfigurationValueRetriever.get(
                configurationValues, ConfigurationKey.PMA_AUTHENTICATION_LOCALE));
    String jwtToken = requestExecutor.authenticate(configurationValues, authenticationBody);
    Token token = TokenDecoder.decode(jwtToken);
    if (Objects.isNull(token)
        || Objects.isNull(token.getCustomerIds())
        || token.getCustomerIds().length == 0) {
      return null;
    }
    Campaign campaign =
        new Campaign(
            PmaCodesFactory.getCampaignId(campaignId, deliveryId),
            Constants.UNKNOWN_VALUE,
            token.getCustomerIds()[0]);
    CampaignCreationResultPage createdCampaignsPage =
        requestExecutor.getCreatedCampaignsPage(configurationValues, campaign);
    boolean existing = Objects.nonNull(createdCampaignsPage) && !createdCampaignsPage.isEmpty();
    CampaignCreationResult campaignCreationResult = null;
    if (existing) {
      campaignCreationResult = createdCampaignsPage.getElements().get(0);
    }
    LastExecutionStatus result = new LastExecutionStatus();
    result.setCampaignId(campaignId);
    result.setDeliveryId(deliveryId);
    result.setExisting(existing);
    boolean started = CampaignChecker.isStarted(campaignCreationResult);
    result.setActive(started);
    result.setEditable(!started);
    result.setRetrievedFromPma(true);
    return result;
  }
}
