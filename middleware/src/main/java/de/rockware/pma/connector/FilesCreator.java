package de.rockware.pma.connector;

import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class FilesCreator {

  public static void main(String... args) {
    try {
      if (Objects.isNull(args) || args.length < 1) {
        System.out.println("Error: path must be specified");
        return;
      }
      List<ConfigurationValue> overriddenConfigurationValues = new ArrayList<>();
      if (args.length == 2 && StringUtils.isNotEmpty(args[1])) {
        processOverriddenConfigurationValues(overriddenConfigurationValues, args[1]);
      }
      String path = args[0];
      createConfigurationFile(path, overriddenConfigurationValues);
      createMappingsFile(path);
    } catch (Throwable e) {
      System.out.printf(
          "Error creating files: %s:\n%s%n", e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
  }

  private static void processOverriddenConfigurationValues(
      List<ConfigurationValue> overriddenConfigurationValues,
      String overriddenConfigurationValuesList) {
    try {
      if (StringUtils.isEmpty(overriddenConfigurationValuesList)) {
        return;
      }
      for (String keyAndValue : overriddenConfigurationValuesList.split(",")) {
        String[] split = keyAndValue.split("\\|");
        if (split.length < 2) {
          continue;
        }
        ConfigurationKey key = ConfigurationKey.valueOf(split[0]);
        String value = split[1];
        overriddenConfigurationValues.add(new ConfigurationValue(key, value));
      }
    } catch (Throwable e) {
      System.out.println("Error processing overridden configuration values");
    }
  }

  private static void createConfigurationFile(
      String dirPath, List<ConfigurationValue> overriddenConfigurationValues) throws Exception {
    checkParentDir(dirPath);
    String path = Paths.get(dirPath, "configuration.json").toString();
    File file = new File(path);
    if (Files.exists(Paths.get(path))) {
      System.out.printf("File configuration.json already existing, deleted: %s%n", file.delete());
    }
    file = Files.createFile(Paths.get(path)).toFile();
    System.out.printf("Writing default configuration values to %s%n", file.getAbsolutePath());
    PrintWriter writer = new PrintWriter(new FileWriter(file));
    List<ConfigurationValue> configurationValues = new ArrayList<>();
    for (ConfigurationKey key : ConfigurationKey.values()) {
      ConfigurationValue overriddenConfigurationValue =
          overriddenConfigurationValues.stream()
              .filter(o -> key.equals(o.getKey()))
              .findFirst()
              .orElse(null);
      if (Objects.nonNull(overriddenConfigurationValue)) {
        configurationValues.add(overriddenConfigurationValue);
        continue;
      }
      configurationValues.add(new ConfigurationValue(key, key.getDefaultValue()));
    }
    writer.write(BeanConverter.serializeCollection(configurationValues));
    writer.close();
    System.out.println("Configuration successfully written");
  }

  private static void createMappingsFile(String dirPath) throws Exception {
    checkParentDir(dirPath);
    String path = Paths.get(dirPath, "mappings.json").toString();
    File file = new File(path);
    if (Files.exists(Paths.get(path))) {
      System.out.printf("File mappings.json already existing, deleted: %s%n", file.delete());
    }
    file = Files.createFile(Paths.get(path)).toFile();
    System.out.printf("Writing sample mappings to %s%n", file.getAbsolutePath());
    PrintWriter writer = new PrintWriter(new FileWriter(file));
    List<Mapping> mappings =
        IntStream.rangeClosed(1, 5)
            .mapToObj(
                i ->
                    new Mapping(
                        i, String.format("Field %d", i), String.format("Field %d M", i), 10))
            .collect(Collectors.toList());
    writer.write(BeanConverter.serializeCollection(mappings));
    writer.close();
    System.out.println("Configuration successfully written");
  }

  private static void checkParentDir(String dirPath) throws IOException {
    Path dir = Paths.get(dirPath);
    if (!dir.toFile().exists()) {
      Files.createDirectory(dir);
    }
  }
}
