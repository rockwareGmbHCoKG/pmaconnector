package de.rockware.pma.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

class FilesCreatorTest {

  private File dir;

  @BeforeEach
  void setUp() throws IOException {
    dir = Files.createTempDirectory("test").toFile();
  }

  @AfterEach
  void tearDown() {
    FileSystemUtils.deleteRecursively(dir);
  }

  @Test
  void noPathProvided() {
    FilesCreator.main();
    assertEquals(0, Objects.requireNonNull(dir.list()).length);
  }

  @Test
  void success() {
    FilesCreator.main(dir.getAbsolutePath());
    List<String> files =
        Arrays.stream(Objects.requireNonNull(dir.list())).sorted().collect(Collectors.toList());
    assertEquals(Arrays.asList("configuration.json", "mappings.json"), files);
  }

  @Test
  void successFilesAlreadyExisting() throws IOException {
    Files.createFile(Paths.get(dir.getAbsolutePath(), "configuration.json"));
    Files.createFile(Paths.get(dir.getAbsolutePath(), "mappings.json"));
    FilesCreator.main(dir.getAbsolutePath());
    List<String> files =
        Arrays.stream(Objects.requireNonNull(dir.list())).sorted().collect(Collectors.toList());
    assertEquals(Arrays.asList("configuration.json", "mappings.json"), files);
  }
}
