package de.rockware.pma.connector.execution.exceptions;

public class CleanupServiceException extends RuntimeException {

  public CleanupServiceException(String message) {
    super(message);
  }

  public CleanupServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
