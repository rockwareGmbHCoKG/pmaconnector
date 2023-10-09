package de.rockware.pma.connector.common.utils;

import java.util.Map;
import java.util.Objects;

public class MapUtils {

  public static <K, V> V putIfNotPresent(Map<K, V> map, K key, V value) {
    if (Objects.isNull(map)) {
      return value;
    }
    if (Objects.isNull(key) || Objects.isNull(value)) {
      return value;
    }
    if (!map.containsKey(key)) {
      map.put(key, value);
    }
    return map.get(key);
  }
}
