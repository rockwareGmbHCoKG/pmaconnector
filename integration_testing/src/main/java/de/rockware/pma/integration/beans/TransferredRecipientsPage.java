package de.rockware.pma.integration.beans;

import static de.rockware.pma.integration.beans.ExecutionErrorsPage.*;
import static de.rockware.pma.integration.beans.TransferredRecipientsPage.*;

import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TransferredRecipientsPage implements Page<TransferredRecipients>, Serializable {
  private List<TransferredRecipients> content;
  private int totalElements;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class TransferredRecipients implements Serializable {
    private String campaignId;
    private String deliveryId;
    private int recipientsCount;

    public TransferredRecipients() {}

    public TransferredRecipients(String campaignId, String deliveryId, int recipientsCount) {
      this.campaignId = campaignId;
      this.deliveryId = deliveryId;
      this.recipientsCount = recipientsCount;
    }
  }
}
