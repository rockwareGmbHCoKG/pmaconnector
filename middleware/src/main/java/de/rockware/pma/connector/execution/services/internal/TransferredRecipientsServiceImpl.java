package de.rockware.pma.connector.execution.services.internal;

import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import de.rockware.pma.connector.execution.repositiories.TransferredRecipientsRepository;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import java.util.List;
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
public class TransferredRecipientsServiceImpl implements TransferredRecipientsService {
  private final TransferredRecipientsRepository transferredRecipientsRepository;

  @Autowired
  public TransferredRecipientsServiceImpl(
      TransferredRecipientsRepository transferredRecipientsRepository) {
    this.transferredRecipientsRepository = transferredRecipientsRepository;
  }

  @Override
  public TransferredRecipients get(String id) {
    if (StringUtils.isEmpty(id)) {
      log.debug("Get called with null id: returning null");
      return null;
    }
    return transferredRecipientsRepository.findById(id).orElse(null);
  }

  @Override
  public Page<TransferredRecipients> getPage(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      int page,
      int size) {
    return transferredRecipientsRepository.findByOptionalParams(
        detailsOid,
        campaignId,
        deliveryId,
        startTime,
        endTime,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time")));
  }

  @Override
  public void save(TransferredRecipients transferredRecipients) {
    if (Objects.isNull(transferredRecipients)) {
      log.debug("Save called with null value: skipped");
      return;
    }
    List<TransferredRecipients> transferredRecipientsOnDb =
        transferredRecipientsRepository.findByCampaignIdAndDeliveryIdAndCorrelationId(
            transferredRecipients.getCampaignId(),
            transferredRecipients.getDeliveryId(),
            transferredRecipients.getCorrelationId());
    if (Objects.nonNull(transferredRecipientsOnDb) && !transferredRecipientsOnDb.isEmpty()) {
      log.debug("Record already inserted: skipped");
      return;
    }
    transferredRecipientsRepository.save(transferredRecipients);
  }

  @Override
  public void delete(String oid) {
    if (StringUtils.isEmpty(oid)) {
      log.debug("Delete called with null oid: skipped");
      return;
    }
    transferredRecipientsRepository.deleteById(oid);
  }

  @Override
  public void clear() {
    transferredRecipientsRepository.deleteAll();
  }
}
