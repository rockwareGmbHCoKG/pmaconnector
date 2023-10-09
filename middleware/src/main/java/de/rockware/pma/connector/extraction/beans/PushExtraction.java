package de.rockware.pma.connector.extraction.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PushExtraction {
  private String deliveryId;
  private String deliveryName;
  private String campaignId;
  private String campaignName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date campaignStartDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date campaignEndDate;

  private String fileName;
  private String isProof;
  private String isRecurring;
  private String creatorEmail;
}
