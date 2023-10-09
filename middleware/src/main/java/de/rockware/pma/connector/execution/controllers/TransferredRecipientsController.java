package de.rockware.pma.connector.execution.controllers;

import de.rockware.pma.connector.common.Constants;
import de.rockware.pma.connector.common.converters.DateConverter;
import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/execution/status/transferred-recipients")
@CrossOrigin(origins = "*")
public class TransferredRecipientsController {
  private final TransferredRecipientsService transferredRecipientsService;

  @Autowired
  public TransferredRecipientsController(
      TransferredRecipientsService transferredRecipientsService) {
    this.transferredRecipientsService = transferredRecipientsService;
  }

  @GetMapping("getPage")
  Page<TransferredRecipients> getPage(
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
    return transferredRecipientsService.getPage(
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
    transferredRecipientsService.clear();
  }
}
