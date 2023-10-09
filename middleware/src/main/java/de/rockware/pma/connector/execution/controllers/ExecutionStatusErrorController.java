package de.rockware.pma.connector.execution.controllers;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.converters.DateConverter;
import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import de.rockware.pma.connector.execution.services.ExecutionStatusErrorService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/execution/status/error")
@CrossOrigin(origins = "*")
public class ExecutionStatusErrorController {
  private final ExecutionStatusErrorService executionStatusErrorService;

  @Autowired
  public ExecutionStatusErrorController(ExecutionStatusErrorService executionStatusErrorService) {
    this.executionStatusErrorService = executionStatusErrorService;
  }

  @GetMapping("getPage")
  Page<ExecutionStatusError> getPage(
      @RequestParam(value = "detailsOid", required = false) String detailsOid,
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
    return executionStatusErrorService.getPage(
        detailsOid,
        campaignId,
        deliveryId,
        DateConverter.toLong(startTime),
        DateConverter.toLong(endTime),
        page,
        size);
  }

  @GetMapping("clear")
  public void clear() {
    executionStatusErrorService.clear();
  }
}
