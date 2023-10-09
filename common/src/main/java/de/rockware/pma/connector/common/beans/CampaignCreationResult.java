package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CampaignCreationResult implements Serializable {
  private int id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdOn;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date changedOn;

  private int version;
  private String campaignType;
  private String campaignName;
  private Integer stateId;
  private CampaignState campaignState;
  private String product;
  private int sendingReasonId;
  private String[] actions;
  private String[] requiredActions;
  private String workflowType;
  private boolean hasDummyName;
  private String campaignIdExt;
  private String individualizationId;
  private String printingProcessId;
  private String deliveryProductId;

  public CampaignCreationResult() {
    // for Jackson purposes
  }

  public CampaignCreationResult(int id, String campaignName, String campaignIdExt) {
    this.id = id;
    this.createdOn = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    this.version = 1;
    this.campaignType = "LONG_TERM";
    this.campaignName = campaignName;
    this.stateId = 110;
    this.sendingReasonId = 10;
    this.actions = new String[] {"EDIT"};
    this.requiredActions =
        new String[] {
          "DEFINE_PRODUCT",
          "DEFINE_SENDING_REASON",
          "ESTIMATE_CAMPAIGN",
          "DEFINE_MAILING_TEMPLATE",
          "DEFINE_VARIABLES"
        };
    this.workflowType = "TRIGGER_COMPLETE";
    this.campaignIdExt = campaignIdExt;
  }

  public CampaignCreationResult(int id, String campaignName, CampaignState campaignState) {
    this.id = id;
    this.createdOn = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    this.version = 1;
    this.campaignType = "LONG_TERM";
    this.campaignName = campaignName;
    this.actions = new String[] {"EDIT"};
    this.campaignState = campaignState;
  }
}
