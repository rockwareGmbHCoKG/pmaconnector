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
public class MailingCreationResult implements Serializable {
  private int id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdOn;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date changedOn;

  private int version;
  private int campaignId;
  private int variableDefVersion;
  private String senderAddress;
  private MailingTemplateType mailingTemplateType;
  private boolean addressMappingsConfirmed;
  private boolean hasIndividualVariables;
  private boolean hasSelectedVariables;
  private boolean addressPageDefined;

  public MailingCreationResult() {
    // for Jackson purposes
  }

  public MailingCreationResult(int id, int campaignId) {
    this.id = id;
    this.createdOn = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    this.version = 1;
    this.campaignId = campaignId;
    this.variableDefVersion = 0;
  }
}
