package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import org.springframework.data.domain.Page;

public interface ExecutionStatusErrorService {

  Page<ExecutionStatusError> getPage(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      int page,
      int size);

  void save(ExecutionStatusError executionStatusError);

  void delete(String oid);

  void clear();
}
