package de.rockware.pma.connector.load.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TransferredRecipientsDetails {
  private String correlationId;
  private int count;
}
