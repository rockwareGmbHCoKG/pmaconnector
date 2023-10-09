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
public class Mailing implements Serializable {
  private String customerId;
  private Integer campaignId;

  public Mailing() {
    // For Jackson purposes
  }

  public Mailing(String customerId, Integer campaignId) {
    this.customerId = customerId;
    this.campaignId = campaignId;
  }

  @JsonIgnore
  public boolean isValid() {
    return StringUtils.isNotEmpty(customerId) && campaignId != 0;
  }
}
