package de.rockware.pma.integration.test;

import de.rockware.pma.integration.beans.Environment;

public interface IntegrationTest {

  void run(Environment env);
}
