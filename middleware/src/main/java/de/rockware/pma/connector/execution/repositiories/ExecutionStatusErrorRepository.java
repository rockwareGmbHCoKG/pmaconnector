package de.rockware.pma.connector.execution.repositiories;

import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExecutionStatusErrorRepository
    extends JpaRepository<ExecutionStatusError, String> {

  @Query(
      "select e from ExecutionStatusError e where (:detailsOid is null or e.detailsOid = :detailsOid) and "
          + "(:campaignId is null or e.campaignId = :campaignId) and "
          + "(:deliveryId is null or e.deliveryId = :deliveryId) and "
          + "(:startTime is null or e.time >= :startTime) and "
          + "(:endTime is null or e.time <= :endTime)")
  Page<ExecutionStatusError> findByOptionalParams(
      String detailsOid,
      String campaignId,
      String deliveryId,
      Long startTime,
      Long endTime,
      Pageable pageable);

  Page<ExecutionStatusError> findByTimeGreaterThanEqualAndTimeLessThanEqual(
      Long startTime, Long endTime, Pageable pageable);

  Page<ExecutionStatusError> findByCampaignIdAndDeliveryId(
      String campaignId, String deliveryId, Pageable pageable);

  List<ExecutionStatusError> findByResourceName(String resourceName);
}
