package de.rockware.pma.connector.startup.status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartupStatusRegistry {
  private boolean startupCompleted = false;

  private StartupStatusRegistry() {
    // private to avoid external instantiation
  }

  private static class LazyHolder {
    static final StartupStatusRegistry INSTANCE = new StartupStatusRegistry();
  }

  public static StartupStatusRegistry getInstance() {
    return LazyHolder.INSTANCE;
  }
}
