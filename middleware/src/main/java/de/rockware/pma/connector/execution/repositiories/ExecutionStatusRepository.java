package de.rockware.pma.connector.execution.repositiories;

import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExecutionStatusRepository
    extends JpaRepository<ExecutionStatus, ExecutionStatusId> {

  @Query(
      "select s from ExecutionStatus s where (:campaignId is null or s.id.campaignId = :campaignId) and "
          + "(:deliveryId is null or s.id.deliveryId = :deliveryId) and "
          + "(:startTime is null or s.lastExecutionTime >= :startTime) and "
          + "(:endTime is null or s.lastExecutionTime <= :endTime)")
  Page<ExecutionStatus> findByOptionalParams(
      String campaignId, String deliveryId, Long startTime, Long endTime, Pageable pageable);

  @Query(
      "select s from ExecutionStatus s where s.id.campaignId = :campaignId and s.id.deliveryId = :deliveryId "
          + "and s.lastExecutionTime = (select max(i.lastExecutionTime) from ExecutionStatus i "
          + "where i.id.campaignId = :campaignId and i.id.deliveryId = :deliveryId)")
  ExecutionStatus findLast(String campaignId, String deliveryId);
}
