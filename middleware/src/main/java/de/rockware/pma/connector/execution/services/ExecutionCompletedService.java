package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.beans.ExecutionContext;

public interface ExecutionCompletedService {

  void process(ExecutionContext executionContext);
}
