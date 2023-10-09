package de.rockware.pma.connector.load.loaders.internal;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.load.loaders.Loader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndefinedLoader implements Loader {
  @Override
  public void load(ExecutionContext executionContext) {
    log.debug("Nothing to do");
  }
}
