package de.rockware.pma.integration.beans;

import static de.rockware.pma.integration.beans.ExecutionDetailsPage.*;
import static de.rockware.pma.integration.beans.ExecutionErrorsPage.*;

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
public class ExecutionErrorsPage implements Page<ExecutionErrors>, Serializable {
  private List<ExecutionErrors> content;
  private int totalElements;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class ExecutionErrors implements Serializable {
    private String campaignId;
    private String deliveryId;
    private String step;
    private String resourceName;
    private String newResourceName;
    private String message;
  }
}
