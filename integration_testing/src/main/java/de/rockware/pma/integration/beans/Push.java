package de.rockware.pma.integration.beans;

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
public class Push implements Serializable {
  private String deliveryId;
  private String deliveryName;
  private String campaignId;
  private String campaignName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date campaignStartDate =
      Date.from(
          LocalDateTime.now()
              .minusMonths(1)
              .minusDays(2)
              .atZone(ZoneId.systemDefault())
              .toInstant());

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date campaignEndDate =
      Date.from(LocalDateTime.now().plusDays(12).atZone(ZoneId.systemDefault()).toInstant());

  private String fileName;
  private String isProof;

  public Push(
      String deliveryId,
      String deliveryName,
      String campaignId,
      String campaignName,
      String fileName,
      String isProof) {
    this.deliveryId = deliveryId;
    this.deliveryName = deliveryName;
    this.campaignId = campaignId;
    this.campaignName = campaignName;
    this.fileName = fileName;
    this.isProof = isProof;
  }
}
