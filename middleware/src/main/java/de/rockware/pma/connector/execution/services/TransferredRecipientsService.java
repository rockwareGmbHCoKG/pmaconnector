package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import org.springframework.data.domain.Page;

public interface TransferredRecipientsService {

  TransferredRecipients get(String oid);

  Page<TransferredRecipients> getPage(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      int page,
      int size);

  void save(TransferredRecipients transferredRecipients);

  void delete(String oid);

  void clear();
}
