package de.rockware.pma.integration.beans;

import static de.rockware.pma.integration.beans.ExecutionPage.*;

import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ExecutionPage implements Page<Execution>, Serializable {
  private List<Execution> content;
  private int totalElements;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class Execution implements Serializable {
    private Id id;
    private String campaignName;
    private String deliveryName;
    private boolean campaignCreated;
    private boolean campaignEditable;
    private boolean campaignStarted;
    private boolean deliveryCreated;
    private boolean fieldsDefinitionDone;

    public Execution() {}

    public Execution(
        Id id,
        String campaignName,
        String deliveryName,
        boolean campaignCreated,
        boolean campaignEditable,
        boolean campaignStarted,
        boolean deliveryCreated,
        boolean fieldsDefinitionDone) {
      this.id = id;
      this.campaignName = campaignName;
      this.deliveryName = deliveryName;
      this.campaignCreated = campaignCreated;
      this.campaignEditable = campaignEditable;
      this.campaignStarted = campaignStarted;
      this.deliveryCreated = deliveryCreated;
      this.fieldsDefinitionDone = fieldsDefinitionDone;
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class Id implements Serializable {
    private String campaignId;
    private String deliveryId;

    public Id() {}

    public Id(String campaignId, String deliveryId) {
      this.campaignId = campaignId;
      this.deliveryId = deliveryId;
    }
  }
}
