package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import org.springframework.data.domain.Page;

public interface ExecutionStatusService {

  ExecutionStatus get(ExecutionStatusId id);

  Page<ExecutionStatus> getPage(
      String campaignId, String deliveryId, Long startTime, Long endTime, int page, int size);

  void save(ExecutionStatus executionStatus);

  void delete(ExecutionStatusId id);

  void clear();
}
