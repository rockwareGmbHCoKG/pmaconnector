package de.rockware.pma.connector.load.loaders.clients;

import de.rockware.pma.connector.common.beans.*;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.load.beans.CampaignCreationResultPage;
import de.rockware.pma.connector.load.beans.MailingCreationResultPage;
import java.util.Collection;
import org.springframework.lang.NonNull;

public interface RequestExecutor {

  String authenticate(
      @NonNull Collection<ConfigurationValue> configurationValues,
      AuthenticationBody authenticationBody);

  CampaignCreationResultPage getCreatedCampaignsPage(
      @NonNull Collection<ConfigurationValue> configurationValues, Campaign campaign);

  CampaignCreationResult createCampaign(
      @NonNull Collection<ConfigurationValue> configurationValues, Campaign campaign);

  MailingCreationResultPage getCreatedDeliveryPage(
      @NonNull Collection<ConfigurationValue> configurationValues,
      Mailing mailing,
      Integer createdCampaignId);

  MailingCreationResult createDelivery(
      @NonNull Collection<ConfigurationValue> configurationValues, Mailing mailing);

  MailingVariableDefinitionsCreationResult getCreatedVariableDefinition(
      @NonNull Collection<ConfigurationValue> configurationValues,
      int deliveryId,
      String customerId);

  MailingVariableDefinitionsCreationResult createVariableDefinitions(
      @NonNull Collection<ConfigurationValue> configurationValues,
      int deliveryId,
      CreateMailingVariableDefinition createMailingVariableDefinition);

  MailingVariableDefinitionsCreationResult updateVariableDefinitions(
      @NonNull Collection<ConfigurationValue> configurationValues,
      int deliveryId,
      UpdateMailingVariableDefinition createMailingVariableDefinition);

  TransferRecipientsResult transferRecipients(
      @NonNull Collection<ConfigurationValue> configurationValues,
      TransferRecipients transferRecipients);
}
