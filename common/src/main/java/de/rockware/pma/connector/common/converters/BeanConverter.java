package de.rockware.pma.connector.common.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.Collection;
import org.springframework.lang.NonNull;

public class BeanConverter {

  public static <T extends Serializable> String serialize(@NonNull T object) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(object);
    } catch (Throwable e) {
      throw new RuntimeException(String.format("Error serializing %s", object));
    }
  }

  public static <T extends Serializable> String serializeCollection(@NonNull Collection<T> object) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(object);
    } catch (Throwable e) {
      throw new RuntimeException(String.format("Error serializing %s", object));
    }
  }

  public static <T extends Serializable> T deserialize(
      @NonNull String serialized, @NonNull Class<T> type) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(serialized, type);
    } catch (Throwable e) {
      throw new RuntimeException(
          String.format("Error deserializing %s to %s", serialized, type.getSimpleName()));
    }
  }
}
