package de.rockware.pma.reststub.controllers;

import static de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.VariableDefinitionElement;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.rockware.pma.connector.common.beans.*;
import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.Page;
import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.reststub.utils.AuthTokenValidator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("automation")
@CrossOrigin(origins = "*")
public class AutomationController {
  private final Cache<String, CampaignCreationResult> campaignCreationResultCache =
      create(CampaignCreationResult.class);
  private final Cache<String, MailingCreationResult> mailingCreationResultCache =
      create(MailingCreationResult.class);

  private final Cache<String, MailingVariableDefinitionsCreationResult>
      variableDefinitionCreationResultCache =
          create(MailingVariableDefinitionsCreationResult.class);

  @GetMapping(value = "longtermcampaigns", produces = "application/json")
  public ResponseEntity<String> getLongTermCampaigns(
      @RequestHeader Map<String, String> headers,
      @RequestParam("customerId") String customerId,
      @RequestParam("campaignIdExt") String campaignIdExt) {
    return execute(
        headers,
        () -> {
          CampaignCreationResult campaignCreationResult =
              campaignCreationResultCache.getIfPresent(getCampaignKey(customerId, campaignIdExt));
          ElementsPage<CampaignCreationResult> result = new ElementsPage<>();
          result.setElements(
              Optional.ofNullable(campaignCreationResult)
                  .map(Collections::singletonList)
                  .orElse(Collections.emptyList()));
          result.setLinks(
              Collections.singletonList(
                  new ElementsPage.Link("self", "http://someurl:8080/somecontext")));
          return new ResponseEntity<>(BeanConverter.serialize(result), HttpStatus.OK);
        });
  }

  @PostMapping(value = "longtermcampaigns", produces = "application/json")
  public ResponseEntity<String> createLongTermCampaign(
      @RequestHeader Map<String, String> headers, @RequestBody String body) {
    return execute(
        headers,
        () -> {
          if (StringUtils.isEmpty(body)) {
            throw new RuntimeException("Empty body");
          }
          Campaign campaign = BeanConverter.deserialize(body, Campaign.class);
          if (Objects.isNull(campaign)) {
            throw new RuntimeException("Null campaign");
          }
          if (!campaign.isValid()) {
            throw new RuntimeException("Invalid campaign");
          }
          int id = RandomUtils.nextInt(100000 - 80000) + 80000;
          CampaignCreationResult campaignCreationResult =
              new CampaignCreationResult(
                  id, campaign.getCampaignName(), campaign.getCampaignIdExt());
          campaignCreationResultCache.put(
              getCampaignKey(campaign.getCustomerId(), campaign.getCampaignIdExt()),
              campaignCreationResult);
          return new ResponseEntity<>(
              BeanConverter.serialize(campaignCreationResult), HttpStatus.OK);
        });
  }

  @GetMapping(value = "mailings", produces = "application/json")
  public ResponseEntity<String> getMailings(
      @RequestHeader Map<String, String> headers,
      @RequestParam("customerId") String customerId,
      @RequestParam("campaignId") String campaignId) {
    return execute(
        headers,
        () -> {
          ElementsPage<MailingCreationResult> result = new ElementsPage<>();
          MailingCreationResult mailingCreationResult =
              mailingCreationResultCache.getIfPresent(customerId);
          result.setElements(
              Optional.ofNullable(mailingCreationResult)
                  .map(Collections::singletonList)
                  .orElse(Collections.emptyList()));
          result.setLinks(
              Collections.singletonList(
                  new ElementsPage.Link("self", "http://someurl:8080/somecontext")));
          return new ResponseEntity<>(BeanConverter.serialize(result), HttpStatus.OK);
        });
  }

  @PostMapping(value = "mailings", produces = "application/json")
  public ResponseEntity<String> createMailing(
      @RequestHeader Map<String, String> headers, @RequestBody String body) {
    return execute(
        headers,
        () -> {
          if (StringUtils.isEmpty(body)) {
            throw new RuntimeException("Empty body");
          }
          Mailing mailing = BeanConverter.deserialize(body, Mailing.class);
          if (Objects.isNull(mailing)) {
            throw new RuntimeException("Null campaign");
          }
          if (!mailing.isValid()) {
            throw new RuntimeException("Invalid mailing");
          }
          int id = RandomUtils.nextInt(100000 - 80000) + 80000;
          MailingCreationResult mailingCreationResult =
              new MailingCreationResult(id, mailing.getCampaignId());
          mailingCreationResultCache.put(mailing.getCustomerId(), mailingCreationResult);
          return new ResponseEntity<>(
              BeanConverter.serialize(mailingCreationResult), HttpStatus.OK);
        });
  }

  @GetMapping(value = "mailings/{mailingId}/variabledefinitions", produces = "application/json")
  public ResponseEntity<String> getVariableDefinitions(
      @RequestHeader Map<String, String> headers,
      @PathVariable(value = "mailingId") String mailingId,
      @RequestParam(value = "customerId") String customerId) {
    return execute(
        headers,
        () -> {
          MailingVariableDefinitionsCreationResult variableDefinitionCreationResult =
              variableDefinitionCreationResultCache.getIfPresent(mailingId);
          MailingVariableDefinitionsCreationResult result =
              new MailingVariableDefinitionsCreationResult();
          result.setElements(
              Optional.ofNullable(variableDefinitionCreationResult)
                  .map(MailingVariableDefinitionsCreationResult::getElements)
                  .orElse(Collections.emptyList()));
          result.setPage(new Page(0, 0, 0, 0));
          return new ResponseEntity<>(BeanConverter.serialize(result), HttpStatus.OK);
        });
  }

  @PostMapping(value = "mailings/{mailingId}/variabledefinitions", produces = "application/json")
  public ResponseEntity<String> createVariableDefinitions(
      @RequestHeader Map<String, String> headers,
      @PathVariable(value = "mailingId") String mailingId,
      @RequestBody String body) {
    return execute(
        headers,
        () -> {
          if (StringUtils.isEmpty(mailingId)) {
            throw new RuntimeException("Empty mailing id");
          }
          if (StringUtils.isEmpty(body)) {
            throw new RuntimeException("Empty body");
          }
          CreateMailingVariableDefinition createMailingVariableDefinition =
              BeanConverter.deserialize(body, CreateMailingVariableDefinition.class);
          if (Objects.isNull(createMailingVariableDefinition)) {
            throw new RuntimeException("Null mailing variable definition");
          }
          if (!createMailingVariableDefinition.isValid()) {
            throw new RuntimeException("Invalid mailing");
          }
          MailingVariableDefinitionsCreationResult mailingVariableDefinitionsCreationResult =
              createMailingVariableDefinitionsCreationResult(createMailingVariableDefinition);
          for (VariableDefinition variableDefinition :
              createMailingVariableDefinition.getCreateVariableDefRequestRepList()) {
            mailingVariableDefinitionsCreationResult.getElements().add(convert(variableDefinition));
          }
          variableDefinitionCreationResultCache.put(
              mailingId, mailingVariableDefinitionsCreationResult);
          return new ResponseEntity<>(
              BeanConverter.serialize(mailingVariableDefinitionsCreationResult), HttpStatus.OK);
        });
  }

  @PostMapping(value = "recipients", produces = "application/json")
  public ResponseEntity<String> recipients(
      @RequestHeader Map<String, String> headers, @RequestBody String body) {
    return execute(
        headers,
        () -> {
          if (StringUtils.isEmpty(body)) {
            throw new RuntimeException("Empty body");
          }
          TransferRecipients transferRecipients =
              BeanConverter.deserialize(body, TransferRecipients.class);
          if (Objects.isNull(transferRecipients)) {
            throw new RuntimeException("Null recipients");
          }
          if (!transferRecipients.isValid()) {
            throw new RuntimeException("Invalid recipients");
          }
          return new ResponseEntity<>(
              BeanConverter.serialize(
                  new TransferRecipientsResult(
                      RandomStringUtils.randomAlphanumeric(36).toUpperCase())),
              HttpStatus.OK);
        });
  }

  @PostMapping(value = "startcampaign", produces = "application/json")
  public ResponseEntity<String> startCampaign(@RequestBody String body) {
    MailingId mailingId = BeanConverter.deserialize(body, MailingId.class);
    String campaignKey = getCampaignKey(mailingId.getCustomerId(), mailingId.getCampaignId());
    CampaignCreationResult campaign = campaignCreationResultCache.getIfPresent(campaignKey);
    if (Objects.isNull(campaign)) {
      return new ResponseEntity<>(
          BeanConverter.serialize(
              new RestStubResponse(String.format("No campaigns for %s", campaignKey), false)),
          HttpStatus.BAD_REQUEST);
    }
    campaign.setStateId(120);
    return new ResponseEntity<>(
        BeanConverter.serialize(
            new RestStubResponse(
                String.format("Campaign %s started successfully", campaignKey), true)),
        HttpStatus.OK);
  }

  @PostMapping(value = "clearcaches", produces = "application/json")
  public ResponseEntity<String> clearCaches() {
    campaignCreationResultCache.invalidateAll();
    mailingCreationResultCache.invalidateAll();
    variableDefinitionCreationResultCache.invalidateAll();
    return new ResponseEntity<>(
        BeanConverter.serialize(new RestStubResponse("Caches cleared successfully", true)),
        HttpStatus.OK);
  }

  private ResponseEntity<String> execute(
      Map<String, String> headers, Supplier<ResponseEntity<String>> executor) {
    ValidationResult validationResult = AuthTokenValidator.validate(headers);
    if (!validationResult.isValid()) {
      return new ResponseEntity<>(validationResult.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    try {
      return executor.get();
    } catch (Throwable e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  private String getCampaignKey(String customerId, String campaignIdExt) {
    return String.format("%s-%s", customerId, campaignIdExt);
  }

  private <V> Cache<String, V> create(Class<V> objectType) {
    return CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build();
  }

  private VariableDefinitionElement convert(VariableDefinition variableDefinition) {
    VariableDefinitionElement element = new VariableDefinitionElement();
    element.setId(RandomUtils.nextInt(100000 - 80000) + 80000);
    element.setLabel(variableDefinition.getLabel());
    element.setSortOrder(variableDefinition.getSortOrder());
    element.setDataType(
        new MailingVariableDefinitionsCreationResult.DataType(variableDefinition.getDataTypeId()));
    return element;
  }

  private MailingVariableDefinitionsCreationResult createMailingVariableDefinitionsCreationResult(
      CreateMailingVariableDefinition createMailingVariableDefinition) {
    MailingVariableDefinitionsCreationResult mailingVariableDefinitionsCreationResult =
        new MailingVariableDefinitionsCreationResult();
    mailingVariableDefinitionsCreationResult.setElements(new ArrayList<>());
    int elements = createMailingVariableDefinition.getCreateVariableDefRequestRepList().size();
    mailingVariableDefinitionsCreationResult.setPage(new Page(elements, elements, 1, 1));
    mailingVariableDefinitionsCreationResult.setLinks(new String[0]);
    return mailingVariableDefinitionsCreationResult;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  private static class RestStubResponse implements Serializable {
    private String message;
    private boolean success;

    public RestStubResponse(String message, boolean success) {
      this.message = message;
      this.success = success;
    }

    public RestStubResponse() {}
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  private static class MailingId implements Serializable {
    private String customerId;
    private String campaignId;

    public MailingId(String customerId, String campaignId) {
      this.customerId = customerId;
      this.campaignId = campaignId;
    }

    public MailingId() {}
  }
}
