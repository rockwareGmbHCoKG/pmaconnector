package de.rockware.pma.integration;

import de.rockware.pma.integration.beans.Environment;
import de.rockware.pma.integration.enumerations.TestName;
import de.rockware.pma.integration.status.TestStatus;
import de.rockware.pma.integration.utils.ResultWriter;
import java.util.Arrays;
import java.util.Objects;

public class IntegrationTestRunner {

  public static void main(String... args) {
    ResultWriter.create();
    ResultWriter.write(String.format("Args: %s\n", Arrays.toString(args)));
    if (Objects.isNull(args) || args.length < 5) {
      ResultWriter.write(
          "Expected 5 properties: Host IP, PMA connector port, Rest Stub port, PMA username, PMA password");
      return;
    }
    Environment env = new Environment();
    env.setHostIp(args[0]);
    env.setPmaConnectorPort(args[1]);
    env.setRestStubPort(args[2]);
    env.setUsername(args[3]);
    env.setPassword(args[4]);
    for (TestName test : TestName.values()) {
      test.getIntegrationTest().run(env);
    }
    if (TestStatus.getInstance().isFailed()) {
      System.out.printf(
          "\nThere are test failures: %s%n", TestStatus.getInstance().getFailedTests());
      System.out.printf("Check '%s' file for details%n", ResultWriter.getResultFilePath());
    } else {
      System.out.println("\nSuccess");
    }
  }
}
