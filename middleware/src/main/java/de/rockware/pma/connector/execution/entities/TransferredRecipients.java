package de.rockware.pma.connector.execution.entities;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
public class TransferredRecipients {
  @Id private String oid;
  private String detailsOid;
  private String campaignId;
  private String deliveryId;
  private String correlationId;
  private Date time;
  private int recipientsCount;

  public TransferredRecipients() {
    // For JPA purposes
  }

  public TransferredRecipients(
      String oid,
      String detailsOid,
      String campaignId,
      String deliveryId,
      String correlationId,
      Date time,
      int recipientsCount) {
    this.oid = oid;
    this.detailsOid = detailsOid;
    this.campaignId = campaignId;
    this.deliveryId = deliveryId;
    this.correlationId = correlationId;
    this.time = time;
    this.recipientsCount = recipientsCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TransferredRecipients that = (TransferredRecipients) o;
    return Objects.equals(oid, that.oid);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
