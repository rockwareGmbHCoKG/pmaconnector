package de.rockware.pma.connector.common.retrievers;

import java.util.Arrays;
import org.springframework.lang.NonNull;

public class EnumRetriever {

  public static <T extends Enum<?>> T getByValue(String value, T[] values, @NonNull T fallback) {
    return Arrays.stream(values).filter(s -> s.name().equals(value)).findFirst().orElse(fallback);
  }
}
