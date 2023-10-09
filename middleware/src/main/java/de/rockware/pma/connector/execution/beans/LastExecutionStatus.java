package de.rockware.pma.connector.execution.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class LastExecutionStatus {
  private String campaignId;
  private String deliveryId;
  private boolean existing;
  private boolean active;
  private boolean editable;
  private boolean retrievedFromPma;
}
