package de.rockware.pma.connector.common.retrievers;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createMappings;
import static org.junit.jupiter.api.Assertions.*;

import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class NotExistingFieldsRetrieverTest {

  @Test
  void nullMappings() {
    assertNull(NotExistingFieldsRetriever.get(null, createSourceFields()));
  }

  @Test
  void emptyMappings() {
    assertNull(NotExistingFieldsRetriever.get(Collections.emptyList(), createSourceFields()));
  }

  @Test
  void nullSourceFields() {
    assertNull(NotExistingFieldsRetriever.get(createMappings(), null));
  }

  @Test
  void emptySourceFields() {
    List<Mapping> mappings = createMappings();
    Set<String> expectedNotExistingFields =
        mappings.stream().map(Mapping::getSource).collect(Collectors.toSet());
    Collection<String> notExistingFields =
        NotExistingFieldsRetriever.get(mappings, Collections.emptyList());
    assertNotNull(notExistingFields);
    assertTrue(
        expectedNotExistingFields.containsAll(notExistingFields)
            && notExistingFields.containsAll(expectedNotExistingFields));
  }

  @Test
  void moreFieldsThanInMappings() {
    List<String> sourceFields = new ArrayList<>(createSourceFields());
    sourceFields.add("Field 6");
    sourceFields.add("Field 7");
    Collection<String> notExistingFields =
        NotExistingFieldsRetriever.get(createMappings(), sourceFields);
    assertNotNull(notExistingFields);
    assertTrue(notExistingFields.isEmpty());
  }

  @Test
  void noNotExistingFields() {
    Collection<String> notExistingFields =
        NotExistingFieldsRetriever.get(createMappings(), createSourceFields());
    assertNotNull(notExistingFields);
    assertTrue(notExistingFields.isEmpty());
  }

  @Test
  void notExistingSourceFields() {
    List<Mapping> mappings = new ArrayList<>(createMappings());
    mappings.add(new Mapping(6, "Field 6", "Field 6 M", 6));
    mappings.add(new Mapping(7, "Field 7", "Field 7 M", 7));
    Collection<String> notExistingFields =
        NotExistingFieldsRetriever.get(mappings, createSourceFields());
    assertNotNull(notExistingFields);
    Collection<String> expectedNotExistingFields = new HashSet<>();
    expectedNotExistingFields.add("Field 6");
    expectedNotExistingFields.add("Field 7");
    assertTrue(
        expectedNotExistingFields.containsAll(notExistingFields)
            && notExistingFields.containsAll(expectedNotExistingFields));
  }

  @Test
  void success() {
    assertTrue(
        Objects.requireNonNull(
                NotExistingFieldsRetriever.get(createMappings(), createSourceFields()))
            .isEmpty());
  }

  private List<String> createSourceFields() {
    return Arrays.asList("Field 1", "Field 2", "Field 3", "Field 4", "Field 5");
  }
}
