package de.rockware.pma.connector.execution.beans;

import de.rockware.pma.connector.common.enumerations.Step;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ExecutionError {
  private Info info;
  private Step step;
  private String newResourceName;
  private String message;
  private Throwable throwable;

  public ExecutionError() {
    this(null, null, null, null, null);
  }

  public ExecutionError(Info info, Step step, String message) {
    this(info, step, null, message, null);
  }

  public ExecutionError(
      Info info, Step step, String newResourceName, String message, Throwable throwable) {
    this.info = info;
    this.step = step;
    this.newResourceName = newResourceName;
    this.message = message;
    this.throwable = throwable;
  }
}
