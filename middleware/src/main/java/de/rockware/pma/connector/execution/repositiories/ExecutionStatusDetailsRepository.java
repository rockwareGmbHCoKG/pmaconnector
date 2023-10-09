package de.rockware.pma.connector.execution.repositiories;

import de.rockware.pma.connector.execution.entities.ExecutionStatusDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExecutionStatusDetailsRepository
    extends JpaRepository<ExecutionStatusDetails, String> {

  @Query(
      "select d from ExecutionStatusDetails d where (:campaignId is null or d.campaignId = :campaignId) and "
          + "(:deliveryId is null or d.deliveryId = :deliveryId) and "
          + "(:startTime is null or d.startTime >= :startTime) and "
          + "(:endTime is null or d.endTime <= :endTime)")
  Page<ExecutionStatusDetails> findByOptionalParams(
      String campaignId, String deliveryId, Long startTime, Long endTime, Pageable pageable);
}
