package de.rockware.pma.connector.execution.exceptions;

public class ExecutionServiceException extends RuntimeException {
  public ExecutionServiceException(String message) {
    super(message);
  }

  public ExecutionServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
