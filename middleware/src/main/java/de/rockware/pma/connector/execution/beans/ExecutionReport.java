package de.rockware.pma.connector.execution.beans;

import de.rockware.pma.connector.execution.entities.ExecutionStatus;
import de.rockware.pma.connector.execution.entities.ExecutionStatusError;
import de.rockware.pma.connector.execution.entities.TransferredRecipients;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ExecutionReport implements Serializable {
  private ExecutionStatus executionStatus;
  private final Collection<ExecutionStatusError> errors = new ArrayList<>();
  private final Collection<TransferredRecipients> transferredRecipients = new ArrayList<>();
}
