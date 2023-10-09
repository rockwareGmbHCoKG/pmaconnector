package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.entities.ExecutionStatusDetails;
import org.springframework.data.domain.Page;

public interface ExecutionStatusDetailsService {

  Page<ExecutionStatusDetails> getPage(
      String campaignId, String deliveryId, Long startTime, Long endTime, int page, int size);

  void save(ExecutionStatusDetails executionStatusDetails);

  void delete(String oid);

  void clear();
}
