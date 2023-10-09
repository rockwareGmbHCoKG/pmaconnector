package de.rockware.pma.connector.configuration.repositories;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository
    extends JpaRepository<ConfigurationValue, ConfigurationKey> {}
