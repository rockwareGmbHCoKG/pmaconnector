package de.rockware.pma.connector.execution.entities;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
public class ExecutionStatusError {
  @Id private String oid;
  private String detailsOid;
  private String campaignId;
  private String deliveryId;
  private String step;
  private String resourceName;
  private String newResourceName;
  @Lob private String message;
  @Lob private String exceptionMessage;
  private Date time;

  public ExecutionStatusError() {
    // for JPA purposes
  }

  public ExecutionStatusError(
      String oid,
      String detailsOid,
      String campaignId,
      String deliveryId,
      String step,
      String resourceName,
      String newResourceName,
      String message,
      String exceptionMessage,
      Date time) {
    this.oid = oid;
    this.detailsOid = detailsOid;
    this.campaignId = campaignId;
    this.deliveryId = deliveryId;
    this.step = step;
    this.resourceName = resourceName;
    this.newResourceName = newResourceName;
    this.message = message;
    this.exceptionMessage = exceptionMessage;
    this.time = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ExecutionStatusError that = (ExecutionStatusError) o;
    return Objects.equals(oid, that.oid);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
