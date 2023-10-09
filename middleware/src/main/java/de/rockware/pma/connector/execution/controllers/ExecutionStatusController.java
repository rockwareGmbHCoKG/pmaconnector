package de.rockware.pma.connector.execution.controllers;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.converters.DateConverter;
import de.rockware.pma.connector.execution.beans.LastExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.services.*;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/execution/status")
@CrossOrigin(origins = "*")
public class ExecutionStatusController {
  private final ExecutionStatusService executionStatusService;
  private final ExecutionStatusDetailsService executionStatusDetailsService;
  private final ExecutionStatusErrorService executionStatusErrorService;
  private final TransferredRecipientsService transferredRecipientsService;
  private final LastExecutionStatusService lastExecutionStatusService;

  @Autowired
  public ExecutionStatusController(
      ExecutionStatusService executionStatusService,
      ExecutionStatusDetailsService executionStatusDetailsService,
      ExecutionStatusErrorService executionStatusErrorService,
      TransferredRecipientsService transferredRecipientsService,
      LastExecutionStatusService lastExecutionStatusService) {
    this.executionStatusService = executionStatusService;
    this.executionStatusDetailsService = executionStatusDetailsService;
    this.executionStatusErrorService = executionStatusErrorService;
    this.transferredRecipientsService = transferredRecipientsService;
    this.lastExecutionStatusService = lastExecutionStatusService;
  }

  @GetMapping("get")
  public ExecutionStatus get(
      @RequestParam("campaignId") String campaignId,
      @RequestParam("deliveryId") String deliveryId) {
    return executionStatusService.get(new ExecutionStatusId(campaignId, deliveryId));
  }

  @GetMapping("getPage")
  Page<ExecutionStatus> getPage(
      @RequestParam(value = "campaignId", required = false) String campaignId,
      @RequestParam(value = "deliveryId", required = false) String deliveryId,
      @RequestParam(value = "startTime", required = false)
          @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN)
          Date startTime,
      @RequestParam(value = "endTime", required = false)
          @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN)
          Date endTime,
      int page,
      int size) {
    return executionStatusService.getPage(
        campaignId,
        deliveryId,
        DateConverter.toLong(startTime),
        DateConverter.toLong(endTime),
        page,
        size);
  }

  @GetMapping("getLast")
  public LastExecutionStatus getLast(
      @RequestParam(value = "campaignId") String campaignId,
      @RequestParam(value = "deliveryId") String deliveryId) {
    return lastExecutionStatusService.get(campaignId, deliveryId);
  }

  @GetMapping("clear")
  public void clear() {
    executionStatusService.clear();
    executionStatusDetailsService.clear();
    executionStatusErrorService.clear();
    transferredRecipientsService.clear();
  }
}
