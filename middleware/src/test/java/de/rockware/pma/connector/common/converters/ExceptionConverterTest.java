package de.rockware.pma.connector.common.converters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

class ExceptionConverterTest {

  @Test
  void nullException() {
    assertNull(ExceptionConverter.convert(null));
  }

  @Test
  void success() {
    Throwable testException = new RuntimeException("Test exception");
    assertFalse(StringUtils.isEmpty(ExceptionConverter.convert(testException)));
  }
}
