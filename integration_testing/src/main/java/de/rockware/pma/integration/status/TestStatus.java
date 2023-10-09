package de.rockware.pma.integration.status;

import java.util.ArrayList;
import java.util.List;

public class TestStatus {
  private static final TestStatus INSTANCE = new TestStatus();
  private final List<String> failedTests = new ArrayList<>();

  public List<String> getFailedTests() {
    return failedTests;
  }

  public boolean isFailed() {
    return !failedTests.isEmpty();
  }

  public static TestStatus getInstance() {
    return INSTANCE;
  }
}
