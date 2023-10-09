package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.execution.entities.ExecutionStatusDetails;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusDetailsRepository;
import de.rockware.pma.connector.execution.services.ExecutionStatusDetailsService;
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
public class ExecutionStatusDetailsServiceImpl implements ExecutionStatusDetailsService {
  private final ExecutionStatusDetailsRepository executionStatusDetailsRepository;

  @Autowired
  public ExecutionStatusDetailsServiceImpl(
      ExecutionStatusDetailsRepository executionStatusDetailsRepository) {
    this.executionStatusDetailsRepository = executionStatusDetailsRepository;
  }

  @Override
  public Page<ExecutionStatusDetails> getPage(
      String campaignId, String deliveryId, Long startTime, Long endTime, int page, int size) {
    return executionStatusDetailsRepository.findByOptionalParams(
        campaignId,
        deliveryId,
        startTime,
        endTime,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "endTime")));
  }

  @Override
  public void save(ExecutionStatusDetails executionStatusDetails) {
    if (Objects.isNull(executionStatusDetails)) {
      log.debug("Save called with null value: skipped");
      return;
    }
    executionStatusDetailsRepository.save(executionStatusDetails);
  }

  @Override
  public void delete(String oid) {
    if (StringUtils.isEmpty(oid)) {
      log.debug("Delete called with null oid: skipped");
      return;
    }
    executionStatusDetailsRepository.deleteById(oid);
  }

  @Override
  public void clear() {
    executionStatusDetailsRepository.deleteAll();
  }
}
