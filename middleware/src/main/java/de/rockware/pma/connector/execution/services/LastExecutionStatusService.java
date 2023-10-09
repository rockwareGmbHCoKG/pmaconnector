package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.beans.LastExecutionStatus;

public interface LastExecutionStatusService {

  LastExecutionStatus get(String campaignId, String deliveryId);
}
