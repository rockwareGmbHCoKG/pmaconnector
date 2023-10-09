package de.rockware.pma.integration.enumerations;

import de.rockware.pma.integration.test.IntegrationTest;
import de.rockware.pma.integration.test.internal.FullTest;
import lombok.Getter;

@Getter
public enum TestName {
  FULL(new FullTest());

  private final IntegrationTest integrationTest;

  TestName(IntegrationTest integrationTest) {
    this.integrationTest = integrationTest;
  }
}
