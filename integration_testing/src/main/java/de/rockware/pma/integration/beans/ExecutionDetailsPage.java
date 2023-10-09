package de.rockware.pma.integration.beans;

import static de.rockware.pma.integration.beans.ExecutionDetailsPage.*;

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
public class ExecutionDetailsPage implements Page<ExecutionDetails>, Serializable {
  private List<ExecutionDetails> content;
  private int totalElements;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class ExecutionDetails implements Serializable {
    private String campaignId;
    private String deliveryId;
    private List<String> extractionMessages;
    private List<String> transformationMessages;
    private List<String> loadingMessages;
  }
}
