package de.rockware.pma.connector.extraction.controllers;

import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.ExecutionService;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.extraction.beans.PushExtraction;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/extraction")
@CrossOrigin(origins = "*")
@Slf4j
public class PushExtractorController {

  private final ExecutionStatusService executionStatusService;
  private final ExecutionService executionService;

  @Autowired
  public PushExtractorController(
      ExecutionStatusService executionStatusService, ExecutionService executionService) {
    this.executionStatusService = executionStatusService;
    this.executionService = executionService;
  }

  @PostMapping("push")
  public void push(@RequestBody PushExtraction body) {
    if (Objects.isNull(body)) {
      log.error("Error: null body");
    }
    ExecutionStatus executionStatus =
        Optional.ofNullable(
                executionStatusService.get(
                    new ExecutionStatusId(body.getCampaignId(), body.getDeliveryId())))
            .orElse(new ExecutionStatus());
    List<String> emailRecipients = new ArrayList<>();
    if (StringUtils.isNotEmpty(body.getCreatorEmail())) {
      emailRecipients.add(body.getCreatorEmail());
    }
    executionService.run(
        Collections.singletonList(
            new Info(
                body.getFileName(),
                body.getCampaignId(),
                body.getCampaignName(),
                body.getDeliveryId(),
                body.getDeliveryName(),
                body.getCampaignStartDate(),
                body.getCampaignEndDate(),
                Boolean.parseBoolean(body.getIsProof()),
                Boolean.parseBoolean(body.getIsRecurring()),
                executionStatus.isCampaignCreated(),
                executionStatus.getCreatedCampaignId(),
                executionStatus.isCampaignEditable(),
                executionStatus.isCampaignStarted(),
                executionStatus.isDeliveryCreated(),
                executionStatus.getCreatedDeliveryId(),
                executionStatus.isFieldsDefinitionDone())),
        emailRecipients);
  }
}
