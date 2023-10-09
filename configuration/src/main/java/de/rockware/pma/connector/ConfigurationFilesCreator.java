package de.rockware.pma.connector;

import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.connector.configuration.beans.KeyValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationType;
import de.rockware.pma.connector.mapping.beans.Mapping;
import de.rockware.pma.connector.mapping.enumerations.MappingsType;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ConfigurationFilesCreator {

  public static void main(String... args) {
    try {
      if (Objects.isNull(args) || args.length < 1) {
        System.out.println("Error: path must be specified");
        return;
      }
      ConfigurationType configurationType = null;
      MappingsType mappingsType = null;
      if (args.length == 3) {
        configurationType = ConfigurationType.getByValue(args[1]);
        mappingsType = MappingsType.getByValue(args[2]);
      }
      Map<ConfigurationKey, KeyValue> defaultConfiguration = new HashMap<>();
      for (ConfigurationKey key : ConfigurationKey.values()) {
        defaultConfiguration.put(key, new KeyValue(key, key.getDefaultValue()));
      }
      writeConfigurations(args[0], defaultConfiguration, configurationType);
      writeMappings(args[0], mappingsType);
    } catch (Throwable e) {
      System.out.printf("Error creating configuration files: %s%n", e.getMessage());
      System.out.println(ExceptionUtils.getStackTrace(e));
    }
  }

  public static void writeConfigurations(
      String path,
      Map<ConfigurationKey, KeyValue> defaultConfiguration,
      ConfigurationType configurationType) {
    boolean allConfigs = Objects.isNull(configurationType);
    File configs = new File(path, allConfigs ? "configs" : "");
    if (allConfigs) {
      if (configs.exists()) {
        deleteDirectory(configs);
      }
      createDirectory(configs);
    }
    for (ConfigurationType configurationTypeValue : ConfigurationType.values()) {
      File directory = new File(configs, allConfigs ? configurationTypeValue.name() : "");
      if (!allConfigs && !configurationTypeValue.equals(configurationType)) {
        continue;
      }
      write(
          configurationTypeValue.name(),
          directory,
          allConfigs,
          "configuration.json",
          BeanConverter.serializeCollection(
              configurationTypeValue
                  .getConfigurationProviderFactory()
                  .get()
                  .create(defaultConfiguration)
                  .stream()
                  .sorted(Comparator.comparing(KeyValue::getKey))
                  .collect(Collectors.toList())));
    }
  }

  private static void writeMappings(String path, MappingsType mappingsType) {
    boolean allMappings = Objects.isNull(mappingsType);
    File mappings = new File(path, allMappings ? "mappings" : "");
    if (allMappings) {
      if (mappings.exists()) {
        deleteDirectory(mappings);
      }
      createDirectory(mappings);
    }
    for (MappingsType mappingsTypeValue : MappingsType.values()) {
      File directory = new File(mappings, allMappings ? mappingsTypeValue.name() : "");
      if (!allMappings && !mappingsTypeValue.equals(mappingsType)) {
        continue;
      }
      write(
          mappingsTypeValue.name(),
          directory,
          allMappings,
          "mappings.json",
          BeanConverter.serializeCollection(
              mappingsTypeValue.getMappingsProviderFactory().get().create().stream()
                  .sorted(Comparator.comparing(Mapping::getId))
                  .collect(Collectors.toList())));
    }
  }

  private static void deleteDirectory(File directory) {
    try {
      FileUtils.deleteDirectory(directory);
    } catch (Throwable e) {
      System.out.printf(
          "Error deleting directory %s: %s%n", directory.getAbsolutePath(), e.getMessage());
    }
  }

  private static void write(
      String type, File directory, boolean createDirectory, String fileName, String content) {
    if (createDirectory) {
      createDirectory(directory);
    }
    File file = createFile(new File(directory, fileName));
    if (Objects.isNull(file)) {
      System.out.println("File not created: skipped");
      return;
    }
    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
      writer.write(content);
    } catch (Throwable e) {
      System.out.printf(
          "Error writing %s file (%s): %s%n", type, file.getAbsolutePath(), e.getMessage());
    }
  }

  private static void createDirectory(File directory) {
    try {
      Files.createDirectory(directory.toPath());
    } catch (Throwable e) {
      System.out.printf("Error creating %s: %s%n", directory.getAbsolutePath(), e.getMessage());
    }
  }

  private static File createFile(File file) {
    try {
      System.out.printf("Creating parent directories: %s%n", file.getParentFile().mkdirs());
      if (file.exists()) {
        System.out.printf("Deleting previous file: %s%n", file.delete());
      }
      File newFile = Files.createFile(file.toPath()).toFile();
      if (newFile.exists()) {
        System.out.printf("File %s created successfully%n", newFile.getAbsolutePath());
      }
      return newFile;
    } catch (Throwable e) {
      System.out.printf("Error creating file %s: %s%n", file.getAbsolutePath(), e.getMessage());
      return null;
    }
  }
}
