package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusRepository;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionStatusServiceImpl implements ExecutionStatusService {
  private final ExecutionStatusRepository executionStatusRepository;

  @Autowired
  public ExecutionStatusServiceImpl(ExecutionStatusRepository executionStatusRepository) {
    this.executionStatusRepository = executionStatusRepository;
  }

  @Override
  public ExecutionStatus get(ExecutionStatusId id) {
    if (Objects.isNull(id)) {
      log.debug("Get called with null id: returning null");
      return null;
    }
    return executionStatusRepository.findById(id).orElse(null);
  }

  @Override
  public Page<ExecutionStatus> getPage(
      String campaignId, String deliveryId, Long startTime, Long endTime, int page, int size) {
    return executionStatusRepository.findByOptionalParams(
        campaignId,
        deliveryId,
        startTime,
        endTime,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastExecutionTime")));
  }

  @Override
  public void save(ExecutionStatus executionStatus) {
    if (Objects.isNull(executionStatus)) {
      log.debug("Save called with null value: skipped");
      return;
    }
    executionStatusRepository.save(executionStatus);
  }

  @Override
  public void delete(ExecutionStatusId id) {
    if (Objects.isNull(id)) {
      log.debug("Delete called with null id: skipped");
      return;
    }
    executionStatusRepository.deleteById(id);
  }

  @Override
  public void clear() {
    executionStatusRepository.deleteAll();
  }
}
