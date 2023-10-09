package de.rockware.pma.connector.execution.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Setter
@ToString
public class ExecutionStatusDetails {
  @Id private String oid;
  private String campaignId;
  private String deliveryId;
  @ElementCollection private List<String> extractionMessages;
  @ElementCollection private List<String> transformationMessages;
  @ElementCollection private List<String> loadingMessages;
  private Date startTime;
  private Date endTime;
  private Long duration;

  @Formula("(SELECT COUNT(*)>0 FROM EXECUTION_STATUS_ERROR E WHERE E.DETAILS_OID = oid)")
  private boolean errors;

  @Formula("(SELECT COUNT(*)>0 FROM TRANSFERRED_RECIPIENTS R WHERE R.DETAILS_OID = oid)")
  private boolean transferredRecipients;

  public ExecutionStatusDetails() {
    // For JPA purposes
  }

  public ExecutionStatusDetails(
      String oid,
      String campaignId,
      String deliveryId,
      List<String> extractionMessages,
      List<String> transformationMessages,
      List<String> loadingMessages,
      Date startTime,
      Date endTime,
      Long duration) {
    this.oid = oid;
    this.campaignId = campaignId;
    this.deliveryId = deliveryId;
    this.extractionMessages = extractionMessages;
    this.transformationMessages = transformationMessages;
    this.loadingMessages = loadingMessages;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
  }
}
