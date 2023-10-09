package de.rockware.pma.connector.common.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TransferRecipientsResult implements Serializable {
  private String correlationId;

  public TransferRecipientsResult() {
    // For Jackson purposes
  }

  public TransferRecipientsResult(String correlationId) {
    this.correlationId = correlationId;
  }
}
