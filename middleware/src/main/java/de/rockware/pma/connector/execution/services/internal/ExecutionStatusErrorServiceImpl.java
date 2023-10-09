package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusErrorRepository;
import de.rockware.pma.connector.execution.services.ExecutionStatusErrorService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionStatusErrorServiceImpl implements ExecutionStatusErrorService {
  private final ExecutionStatusErrorRepository executionStatusErrorRepository;

  @Autowired
  public ExecutionStatusErrorServiceImpl(
      ExecutionStatusErrorRepository executionStatusErrorRepository) {
    this.executionStatusErrorRepository = executionStatusErrorRepository;
  }

  @Override
  public Page<ExecutionStatusError> getPage(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      int page,
      int size) {
    return executionStatusErrorRepository.findByOptionalParams(
        detailsOid,
        campaignId,
        deliveryId,
        startTime,
        endTime,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time")));
  }

  @Override
  public void save(ExecutionStatusError executionStatusError) {
    if (Objects.isNull(executionStatusError)) {
      log.debug("Save called with null value: skipped");
      return;
    }
    executionStatusErrorRepository.save(executionStatusError);
  }

  @Override
  public void delete(String oid) {
    if (StringUtils.isEmpty(oid)) {
      log.debug("Delete called with null oid: skipped");
      return;
    }
    executionStatusErrorRepository.deleteById(oid);
  }

  @Override
  public void clear() {
    executionStatusErrorRepository.deleteAll();
  }
}
