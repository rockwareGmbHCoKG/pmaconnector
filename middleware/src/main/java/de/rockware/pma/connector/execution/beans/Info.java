package de.rockware.pma.connector.execution.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import java.util.Date;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Info {
  private String resourceName;
  private String campaignId;
  private String campaignName;
  private String deliveryId;
  private String deliveryName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date endDate;

  private boolean proof = true;
  private boolean recurring = false;
  private boolean campaignCreated;
  private Integer createdCampaignId;
  private boolean campaignEditable;
  private boolean campaignStarted;
  private boolean deliveryCreated;
  private Integer createdDeliveryId;
  private boolean fieldsDefinitionDone;

  public Info() {
    // For Jackson purposes
  }

  public Info(
      String resourceName,
      String campaignId,
      String campaignName,
      String deliveryId,
      String deliveryName,
      Date startDate,
      Date endDate,
      boolean proof,
      boolean recurring,
      boolean campaignCreated,
      Integer createdCampaignId,
      boolean campaignEditable,
      boolean campaignStarted,
      boolean deliveryCreated,
      Integer createdDeliveryId,
      boolean fieldsDefinitionDone) {
    this.resourceName = resourceName;
    this.deliveryId = deliveryId;
    this.deliveryName = deliveryName;
    this.campaignId = campaignId;
    this.campaignName = campaignName;
    this.startDate = startDate;
    this.endDate = endDate;
    this.proof = proof;
    this.recurring = recurring;
    this.campaignCreated = campaignCreated;
    this.createdCampaignId = createdCampaignId;
    this.campaignEditable = campaignEditable;
    this.campaignStarted = campaignStarted;
    this.deliveryCreated = deliveryCreated;
    this.createdDeliveryId = createdDeliveryId;
    this.fieldsDefinitionDone = fieldsDefinitionDone;
  }

  @JsonIgnore
  public Info copy() {
    return new Info(
        resourceName,
        campaignId,
        campaignName,
        deliveryId,
        deliveryName,
        startDate,
        endDate,
        proof,
        recurring,
        campaignCreated,
        createdCampaignId,
        campaignEditable,
        campaignStarted,
        deliveryCreated,
        createdDeliveryId,
        fieldsDefinitionDone);
  }

  @JsonIgnore
  public void update(ExecutionStatus executionStatus) {
    if (Objects.isNull(executionStatus)) {
      return;
    }
    this.campaignCreated = executionStatus.isCampaignCreated();
    this.createdCampaignId = executionStatus.getCreatedCampaignId();
    this.campaignEditable = executionStatus.isCampaignEditable();
    this.campaignStarted = executionStatus.isCampaignStarted();
    this.deliveryCreated = executionStatus.isDeliveryCreated();
    this.createdDeliveryId = executionStatus.getCreatedDeliveryId();
    this.fieldsDefinitionDone = executionStatus.isFieldsDefinitionDone();
  }
}
