package de.rockware.pma.connector.mapping.services.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createMappings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.mapping.repositories.MappingRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingServiceImplTest {
  @Mock private MappingRepository mappingRepository;
  private MappingServiceImpl sut;

  @BeforeEach
  void setUp() {
    this.sut = new MappingServiceImpl(mappingRepository);
  }

  @Test
  void getAll() {
    List<Mapping> mappings = createMappings();
    when(mappingRepository.findAll()).thenReturn(mappings);
    assertEquals(mappings, sut.getAll());
    verify(mappingRepository).findAll();
  }

  @Test
  void setAllNoValues() {
    sut.setAll();
    verify(mappingRepository).deleteAll();
    verifyNoMoreInteractions(mappingRepository);
  }

  @Test
  void setAllMultipleValues() {
    Mapping first = new Mapping(1, "Field 1", "Field 1 M", 1);
    Mapping second = new Mapping(2, "Field 2", "Field 2 M", 2);
    Mapping third = new Mapping(3, "Field 3", "Field 3 M", 3);
    sut.setAll(first, second, third);
    verify(mappingRepository).deleteAll();
    verify(mappingRepository).saveAll(eq(Arrays.asList(first, second, third)));
  }

  @Test
  void clearSuccess() {
    sut.clear();
    verify(mappingRepository).deleteAll();
  }
}
