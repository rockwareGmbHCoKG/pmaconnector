package de.rockware.pma.connector.load.loaders;

import de.rockware.pma.connector.execution.beans.ExecutionContext;

public interface Loader {

  void load(ExecutionContext executionContext);
}
