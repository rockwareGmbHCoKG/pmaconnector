package de.rockware.pma.integration.enumerations;

import lombok.Getter;

@Getter
public enum ScriptCollection {
  WINDOWS("cmd /c start copy_files.bat"),
  UNIX("sh copy_files.sh");

  private final String copyFilesCommand;

  ScriptCollection(String copyFilesCommand) {
    this.copyFilesCommand = copyFilesCommand;
  }
}
