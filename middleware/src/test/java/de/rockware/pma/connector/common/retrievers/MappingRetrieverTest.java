package de.rockware.pma.connector.common.retrievers;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createMappings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class MappingRetrieverTest {

  @Test
  void getNullParameters() {
    assertNull(MappingRetriever.getBySourceField(null, null));
  }

  @Test
  void getBySourceFieldNullConfigurationValues() {
    assertNull(MappingRetriever.getBySourceField(null, "Field 1"));
  }

  @Test
  void getBySourceFieldEmptyMappingss() {
    assertNull(MappingRetriever.getBySourceField(Collections.emptyList(), "Field 1"));
  }

  @Test
  void getBySourceFieldNullKey() {
    assertNull(MappingRetriever.getBySourceField(createMappings(), null));
  }

  @Test
  void getBySourceFieldMappingNotFound() {
    assertNull(MappingRetriever.getBySourceField(createMappings(), "Unknown field"));
  }

  @Test
  void getBySourceFieldSuccess() {
    assertEquals(
        new Mapping(1, "Field 1", "Field 1 M", 1),
        MappingRetriever.getBySourceField(createMappings(), "Field 1"));
  }
}
