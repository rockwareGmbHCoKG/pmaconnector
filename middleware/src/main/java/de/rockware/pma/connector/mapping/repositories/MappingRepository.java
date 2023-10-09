package de.rockware.pma.connector.mapping.repositories;

import de.rockware.pma.connector.mapping.entities.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MappingRepository extends JpaRepository<Mapping, Long> {}
