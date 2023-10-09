package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MapUtilsTest {

  @Test
  void putIfNotPresent_NullMap() {
    assertEquals("VALUE", MapUtils.putIfNotPresent(null, "KEY", "VALUE"));
  }

  @Test
  void putIfNotPresent_NullKey() {
    assertEquals("VALUE", MapUtils.putIfNotPresent(new HashMap<>(), null, "VALUE"));
  }

  @Test
  void putIfNotPresent_NullValue() {
    assertNull(MapUtils.putIfNotPresent(new HashMap<>(), "KEY", null));
  }

  @Test
  void putIfNotPresent_NotPresent() {
    Map<String, Object> map = new HashMap<>();
    assertEquals("VALUE", MapUtils.putIfNotPresent(map, "KEY", "VALUE"));
    assertEquals("VALUE", map.get("KEY"));
  }

  @Test
  void putIfNotPresent_AlreadyPresent() {
    Map<String, Object> map = new HashMap<>();
    map.put("KEY", "ANOTHER_VALUE");
    assertEquals("ANOTHER_VALUE", MapUtils.putIfNotPresent(map, "KEY", "VALUE"));
    assertEquals("ANOTHER_VALUE", map.get("KEY"));
  }
}
