package de.rockware.pma.connector.execution.repositiories;

import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransferredRecipientsRepository
    extends JpaRepository<TransferredRecipients, String> {

  @Query(
      "select t from TransferredRecipients t where (:detailsOid is null or t.detailsOid = :detailsOid) and "
          + "(:campaignId is null or t.campaignId = :campaignId) and "
          + "(:deliveryId is null or t.deliveryId = :deliveryId) and "
          + "(:startTime is null or t.time >= :startTime) and "
          + "(:endTime is null or t.time <= :endTime)")
  Page<TransferredRecipients> findByOptionalParams(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      Pageable pageable);

  List<TransferredRecipients> findByCampaignIdAndDeliveryIdAndCorrelationId(
      String campaignId, String deliveryId, String correlationId);
}
