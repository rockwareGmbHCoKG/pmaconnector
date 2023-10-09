package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Campaign implements Serializable {
  private String campaignIdExt;
  private String campaignName;
  private String customerId;

  public Campaign() {
    // For Jackson purposes
  }

  public Campaign(String campaignIdExt, String campaignName, String customerId) {
    this.campaignIdExt = campaignIdExt;
    this.campaignName = campaignName;
    this.customerId = customerId;
  }

  @JsonIgnore
  public boolean isValid() {
    return StringUtils.isNotEmpty(campaignIdExt)
        && StringUtils.isNotEmpty(campaignName)
        && StringUtils.isNotEmpty(customerId);
  }
}
