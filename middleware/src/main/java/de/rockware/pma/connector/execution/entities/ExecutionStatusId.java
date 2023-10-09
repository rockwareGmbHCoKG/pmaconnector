package de.rockware.pma.connector.execution.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@Embeddable
public class ExecutionStatusId implements Serializable {
  private String campaignId;
  private String deliveryId;

  public ExecutionStatusId() {
    // for JPA purposes
  }

  public ExecutionStatusId(String campaignId, String deliveryId) {
    this.campaignId = campaignId;
    this.deliveryId = deliveryId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ExecutionStatusId that = (ExecutionStatusId) o;
    return Objects.equals(campaignId, that.campaignId)
        && Objects.equals(deliveryId, that.deliveryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(campaignId, deliveryId);
  }
}
