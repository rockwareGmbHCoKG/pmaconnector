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
public class CampaignState implements Serializable {
  private int id;
  private String label;

  public CampaignState() {
    // For JPA purposes
  }

  public CampaignState(int id, String label) {
    this.id = id;
    this.label = label;
  }
}
