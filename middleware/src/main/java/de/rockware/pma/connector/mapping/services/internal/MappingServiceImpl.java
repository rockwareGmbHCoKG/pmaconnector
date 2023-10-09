package de.rockware.pma.connector.mapping.services.internal;

import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.mapping.repositories.MappingRepository;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MappingServiceImpl implements ReadWriteService<Mapping> {

  private final MappingRepository mappingRepository;

  @Autowired
  public MappingServiceImpl(MappingRepository mappingRepository) {
    this.mappingRepository = mappingRepository;
  }

  @Override
  public Collection<Mapping> getAll() {
    return mappingRepository.findAll();
  }

  @Override
  public void setAll(Mapping... mappings) {
    clear();
    if (Objects.isNull(mappings) || mappings.length == 0) {
      log.debug("No mappings to be added");
      return;
    }
    mappingRepository.saveAll(Arrays.asList(mappings));
  }

  @Override
  public void clear() {
    mappingRepository.deleteAll();
  }
}
