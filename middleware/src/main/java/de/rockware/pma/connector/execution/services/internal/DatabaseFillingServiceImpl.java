package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.common.converters.ExceptionConverter;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.utils.OidGenerator;
import de.rockware.pma.connector.execution.entities.*;
import de.rockware.pma.connector.execution.services.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseFillingServiceImpl implements DatabaseFillingService {
  private final ExecutionStatusService executionStatusService;
  private final ExecutionStatusDetailsService executionStatusDetailsService;
  private final ExecutionStatusErrorService executionStatusErrorService;
  private final TransferredRecipientsService transferredRecipientsService;

  @Autowired
  public DatabaseFillingServiceImpl(
      ExecutionStatusService executionStatusService,
      ExecutionStatusDetailsService executionStatusDetailsService,
      ExecutionStatusErrorService executionStatusErrorService,
      TransferredRecipientsService transferredRecipientsService) {
    this.executionStatusService = executionStatusService;
    this.executionStatusDetailsService = executionStatusDetailsService;
    this.executionStatusErrorService = executionStatusErrorService;
    this.transferredRecipientsService = transferredRecipientsService;
  }

  @Override
  public void fill() {
    Random random = new Random();
    for (int i = 0; i < 20; i++) {
      String detailsOid = OidGenerator.generate();
      String campaignId = RandomStringUtils.randomAlphanumeric(3).toUpperCase();
      String deliveryId = RandomStringUtils.randomAlphanumeric(3).toUpperCase();
      Date startTime =
          Date.from(
              LocalDateTime.now()
                  .minusDays(random.nextInt(100))
                  .minusHours(random.nextInt(24))
                  .minusMinutes(random.nextInt(60))
                  .atZone(ZoneId.systemDefault())
                  .toInstant());
      ExecutionStatus executionStatus =
          new ExecutionStatus(
              new ExecutionStatusId(campaignId, deliveryId),
              "Test campaign " + i,
              Date.from(
                  LocalDate.of(2021, 1, 1)
                      .plusMonths(i)
                      .atStartOfDay()
                      .atZone(ZoneId.systemDefault())
                      .toInstant()),
              Date.from(
                  LocalDate.of(2021, 12, 31)
                      .plusMonths(i)
                      .atStartOfDay()
                      .atZone(ZoneId.systemDefault())
                      .toInstant()),
              "Test delivery " + i,
              random.nextBoolean(),
              createPositiveInteger(random),
              random.nextBoolean(),
              random.nextBoolean(),
              random.nextBoolean(),
              createPositiveInteger(random),
              random.nextBoolean(),
              startTime);
      executionStatusService.save(executionStatus);
      Integer transferredRecipientsSize = random.nextBoolean() ? random.nextInt(101) : null;
      Date endTime = Date.from(startTime.toInstant().plusMillis(random.nextInt(120000)));
      executionStatusDetailsService.save(
          new ExecutionStatusDetails(
              detailsOid,
              campaignId,
              deliveryId,
              createMessageList(false, null, null, false, null),
              createMessageList(false, null, null, false, null),
              createMessageList(
                  random.nextBoolean(),
                  createPositiveInteger(random),
                  createPositiveInteger(random),
                  random.nextBoolean(),
                  transferredRecipientsSize),
              executionStatus.getLastExecutionTime(),
              endTime,
              endTime.getTime() - executionStatus.getLastExecutionTime().getTime()));
      if (random.nextBoolean()) {
        for (int j = 0; j < random.nextInt(5); j++) {
          if (!random.nextBoolean()) {
            continue;
          }
          executionStatusErrorService.save(
              new ExecutionStatusError(
                  OidGenerator.generate(),
                  detailsOid,
                  campaignId,
                  deliveryId,
                  Step.values()[random.nextInt(3)].name(),
                  campaignId + "_" + deliveryId + "_example.csv",
                  null,
                  "An error occured during execution",
                  ExceptionConverter.convert(new RuntimeException("Generic exception")),
                  executionStatus.getLastExecutionTime()));
        }
      }
      if (Objects.nonNull(transferredRecipientsSize) && transferredRecipientsSize > 0) {
        for (int k = 0; k < 5; k++) {
          if (!random.nextBoolean()) {
            continue;
          }
          transferredRecipientsService.save(
              new TransferredRecipients(
                  OidGenerator.generate(),
                  detailsOid,
                  campaignId,
                  deliveryId,
                  RandomStringUtils.randomAlphanumeric(36).toUpperCase(),
                  executionStatus.getLastExecutionTime(),
                  transferredRecipientsSize));
        }
      }
    }
  }

  private int createPositiveInteger(Random random) {
    return Math.abs(random.nextInt());
  }

  private List<String> createMessageList(
      boolean create,
      Integer campaignCreated,
      Integer deliveryCreated,
      boolean fieldsDefinitionsDone,
      Integer transferredRecipientsSize) {
    List<String> messages = new ArrayList<>();
    if (!create) {
      return messages;
    }
    if (Objects.nonNull(campaignCreated)) {
      messages.add(String.format("Campaign created successfully (ID %d)", campaignCreated));
    }
    if (Objects.nonNull(deliveryCreated)) {
      messages.add(String.format("Delivery created successfully (ID %d)", deliveryCreated));
    }
    if (fieldsDefinitionsDone) {
      messages.add("Fields definitions done successfully");
    }
    if (Objects.nonNull(transferredRecipientsSize)) {
      messages.add(
          String.format("Successfully transferred %d recipients", transferredRecipientsSize));
    }
    return messages;
  }
}
