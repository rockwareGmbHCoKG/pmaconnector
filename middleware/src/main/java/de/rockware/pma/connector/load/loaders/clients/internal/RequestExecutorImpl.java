package de.rockware.pma.connector.load.loaders.clients.internal;

import de.rockware.pma.connector.common.beans.*;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.utils.UrlBuilder;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.load.beans.CampaignCreationResultPage;
import de.rockware.pma.connector.load.beans.MailingCreationResultPage;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RequestExecutorImpl implements RequestExecutor {

  private String jwtToken;

  @Override
  public String authenticate(
      Collection<ConfigurationValue> configurationValues, AuthenticationBody authenticationBody) {
    JwtTokenHolder jwtTokenHolder =
        getWebClient(configurationValues)
            .post()
            .uri(
                Objects.requireNonNull(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_AUTHENTICATION_URL_FRAGMENT)))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(authenticationBody), AuthenticationBody.class)
            .retrieve()
            .onStatus(
                s -> s.is4xxClientError() || s.is5xxServerError(),
                r -> createException(r, "Error authenticating"))
            .bodyToMono(JwtTokenHolder.class)
            .block();
    this.jwtToken =
        Optional.ofNullable(jwtTokenHolder).map(JwtTokenHolder::getJwtToken).orElse(null);
    return jwtToken;
  }

  @Override
  public CampaignCreationResultPage getCreatedCampaignsPage(
      Collection<ConfigurationValue> configurationValues, Campaign campaign) {
    return getWebClient(configurationValues)
        .get()
        .uri(
            UrlBuilder.appendFragments(
                Collections.singletonList(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_CAMPAIGN_URL_FRAGMENT)),
                Arrays.asList(
                    String.format("customerId=%s", campaign.getCustomerId()),
                    String.format("campaignIdExt=%s", campaign.getCampaignIdExt()))))
        .headers(fillHeaders(jwtToken))
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing get campaign"))
        .bodyToMono(CampaignCreationResultPage.class)
        .block();
  }

  @Override
  public CampaignCreationResult createCampaign(
      Collection<ConfigurationValue> configurationValues, Campaign campaign) {
    return getWebClient(configurationValues)
        .post()
        .uri(
            Objects.requireNonNull(
                ConfigurationValueRetriever.get(
                    configurationValues,
                    ConfigurationKey.PMA_SERVICE_CREATE_CAMPAIGN_URL_FRAGMENT)))
        .headers(fillHeaders(jwtToken))
        .body(Mono.just(campaign), Campaign.class)
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing create campaign"))
        .bodyToMono(CampaignCreationResult.class)
        .block();
  }

  @Override
  public MailingCreationResultPage getCreatedDeliveryPage(
      Collection<ConfigurationValue> configurationValues,
      Mailing mailing,
      Integer createdCampaignId) {
    return getWebClient(configurationValues)
        .get()
        .uri(
            UrlBuilder.appendFragments(
                Collections.singletonList(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT)),
                Arrays.asList(
                    String.format("customerId=%s", mailing.getCustomerId()),
                    String.format("campaignId=%d", createdCampaignId))))
        .headers(fillHeaders(jwtToken))
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing get campaign"))
        .bodyToMono(MailingCreationResultPage.class)
        .block();
  }

  @Override
  public MailingCreationResult createDelivery(
      Collection<ConfigurationValue> configurationValues, Mailing mailing) {
    return getWebClient(configurationValues)
        .post()
        .uri(
            Objects.requireNonNull(
                ConfigurationValueRetriever.get(
                    configurationValues,
                    ConfigurationKey.PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT)))
        .headers(fillHeaders(jwtToken))
        .body(Mono.just(mailing), Mailing.class)
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing create delivery"))
        .bodyToMono(MailingCreationResult.class)
        .block();
  }

  @Override
  public MailingVariableDefinitionsCreationResult getCreatedVariableDefinition(
      Collection<ConfigurationValue> configurationValues, int deliveryId, String customerId) {
    return getWebClient(configurationValues)
        .get()
        .uri(
            UrlBuilder.appendFragments(
                Arrays.asList(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT),
                    String.valueOf(deliveryId),
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT)),
                Collections.singletonList(String.format("customerId=%s", customerId))))
        .headers(fillHeaders(jwtToken))
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing get campaign"))
        .bodyToMono(MailingVariableDefinitionsCreationResult.class)
        .block();
  }

  @Override
  public MailingVariableDefinitionsCreationResult createVariableDefinitions(
      Collection<ConfigurationValue> configurationValues,
      int deliveryId,
      CreateMailingVariableDefinition createMailingVariableDefinition) {
    return getWebClient(configurationValues)
        .post()
        .uri(
            UrlBuilder.appendFragments(
                Arrays.asList(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT),
                    String.valueOf(deliveryId),
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT)),
                Collections.emptyList()))
        .headers(fillHeaders(jwtToken))
        .body(Mono.just(createMailingVariableDefinition), CreateMailingVariableDefinition.class)
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing create variable definition"))
        .bodyToMono(MailingVariableDefinitionsCreationResult.class)
        .block();
  }

  @Override
  public MailingVariableDefinitionsCreationResult updateVariableDefinitions(
      Collection<ConfigurationValue> configurationValues,
      int deliveryId,
      UpdateMailingVariableDefinition updateMailingVariableDefinition) {
    return getWebClient(configurationValues)
        .put()
        .uri(
            UrlBuilder.appendFragments(
                Arrays.asList(
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_MAILINGS_URL_FRAGMENT),
                    String.valueOf(deliveryId),
                    ConfigurationValueRetriever.get(
                        configurationValues,
                        ConfigurationKey.PMA_SERVICE_CREATE_VARIABLE_DEFINITIONS_URL_FRAGMENT)),
                Collections.emptyList()))
        .headers(fillHeaders(jwtToken))
        .body(Mono.just(updateMailingVariableDefinition), CreateMailingVariableDefinition.class)
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing create variable definition"))
        .bodyToMono(MailingVariableDefinitionsCreationResult.class)
        .block();
  }

  @Override
  public TransferRecipientsResult transferRecipients(
      Collection<ConfigurationValue> configurationValues, TransferRecipients transferRecipients) {
    return getWebClient(configurationValues)
        .post()
        .uri(
            Objects.requireNonNull(
                ConfigurationValueRetriever.get(
                    configurationValues,
                    ConfigurationKey.PMA_SERVICE_TRANSFER_RECIPIENTS_URL_FRAGMENT)))
        .headers(fillHeaders(jwtToken))
        .body(Mono.just(transferRecipients), TransferRecipients.class)
        .retrieve()
        .onStatus(
            s -> s.is4xxClientError() || s.is5xxServerError(),
            r -> createException(r, "Error executing transfer recipients"))
        .bodyToMono(TransferRecipientsResult.class)
        .block();
  }

  private WebClient getWebClient(Collection<ConfigurationValue> configurationValues) {
    return WebClient.builder()
        .baseUrl(
            Objects.requireNonNull(
                ConfigurationValueRetriever.get(
                    configurationValues, ConfigurationKey.PMA_SERVICE_BASE_URL)))
        .clientConnector(
            new ReactorClientHttpConnector(
                HttpClient.create()
                    .responseTimeout(
                        Duration.ofMillis(
                            ConfigurationValueRetriever.getAsInt(
                                configurationValues, ConfigurationKey.PMA_SERVICE_TIMEOUT)))
                    .wiretap(true)))
        .build();
  }

  private Consumer<HttpHeaders> fillHeaders(String token) {
    return h -> {
      h.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
      h.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      h.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    };
  }

  private Mono<Throwable> createException(ClientResponse response, String errorTitle) {
    return response
        .bodyToMono(String.class)
        .flatMap(
            b ->
                Mono.error(
                    new RequestExecutorException(
                        String.format(
                            "%s: status=%s, message=%s", errorTitle, response.statusCode(), b))));
  }

  private static class RequestExecutorException extends RuntimeException {

    public RequestExecutorException(String message) {
      super(message);
    }
  }
}
