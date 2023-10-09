package de.rockware.pma.connector.execution.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rockware.pma.connector.execution.beans.Info;
import java.util.Date;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
public class ExecutionStatus {

  @EmbeddedId private ExecutionStatusId id;
  private String campaignName;
  private Date campaignStartDate;
  private Date campaignEndDate;
  private String deliveryName;
  private boolean campaignCreated;
  private Integer createdCampaignId;
  private boolean campaignEditable;
  private boolean campaignStarted;
  private boolean deliveryCreated;
  private Integer createdDeliveryId;
  private boolean fieldsDefinitionDone;
  private Date lastExecutionTime;

  public ExecutionStatus() {
    // for JPA purposes
  }

  public ExecutionStatus(
      ExecutionStatusId id,
      String campaignName,
      Date campaignStartDate,
      Date campaignEndDate,
      String deliveryName,
      boolean campaignCreated,
      Integer createdCampaignId,
      boolean campaignEditable,
      boolean campaignStarted,
      boolean deliveryCreated,
      Integer createdDeliveryId,
      boolean fieldsDefinitionDone,
      Date lastExecutionTime) {
    this.id = id;
    this.campaignName = campaignName;
    this.campaignStartDate = campaignStartDate;
    this.campaignEndDate = campaignEndDate;
    this.deliveryName = deliveryName;
    this.campaignCreated = campaignCreated;
    this.createdCampaignId = createdCampaignId;
    this.campaignEditable = campaignEditable;
    this.campaignStarted = campaignStarted;
    this.deliveryCreated = deliveryCreated;
    this.createdDeliveryId = createdDeliveryId;
    this.fieldsDefinitionDone = fieldsDefinitionDone;
    this.lastExecutionTime = lastExecutionTime;
  }

  @JsonIgnore
  public void update(Info info, Date time) {
    this.campaignCreated = info.isCampaignCreated();
    this.createdCampaignId = info.getCreatedCampaignId();
    this.campaignEditable = info.isCampaignEditable();
    this.campaignStarted = info.isCampaignStarted();
    this.deliveryCreated = info.isDeliveryCreated();
    this.createdDeliveryId = info.getCreatedDeliveryId();
    this.fieldsDefinitionDone = info.isFieldsDefinitionDone();
    this.lastExecutionTime = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ExecutionStatus that = (ExecutionStatus) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
