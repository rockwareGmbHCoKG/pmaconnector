package de.rockware.pma.connector.mapping.providers;

import de.rockware.pma.connector.mapping.beans.Mapping;
import java.util.Collection;

public interface MappingsProvider {

  Collection<Mapping> create();
}
