package de.rockware.pma.integration.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ResultWriter {

  public static void create() {
    String resultFileName = "";
    try {
      Path resultFile = getResultFilePath();
      resultFileName = resultFile.getFileName().toString();
      if (resultFile.toFile().exists()) {
        Files.delete(resultFile);
      }
      Files.createFile(resultFile);
    } catch (Throwable e) {
      throw new RuntimeException(
          String.format("Error creating '%s': %s", resultFileName, e.getMessage()), e);
    }
  }

  public static void write(String result) {
    try {
      Path resultFile = getResultFilePath();
      if (!resultFile.toFile().exists()) {
        throw new RuntimeException(
            String.format("Error: file '%s' does not exist!", resultFile.getFileName().toString()));
      }
      Files.write(resultFile, result.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    } catch (Throwable e) {
      throw new RuntimeException(String.format("Error writing result: %s", e.getMessage()), e);
    }
  }

  public static Path getResultFilePath() {
    String userDir = System.getProperty("user.dir");
    return Paths.get(userDir, "integration_testing", "target", "integration_tests_result.txt");
  }
}
