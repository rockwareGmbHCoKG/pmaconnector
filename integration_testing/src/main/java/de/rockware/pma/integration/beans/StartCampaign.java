package de.rockware.pma.integration.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StartCampaign implements Serializable {
  private String customerId;
  private String campaignId;

  public StartCampaign(String customerId, String campaignId) {
    this.customerId = customerId;
    this.campaignId = campaignId;
  }
}
