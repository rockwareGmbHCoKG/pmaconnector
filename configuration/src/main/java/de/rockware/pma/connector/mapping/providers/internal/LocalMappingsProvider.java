package de.rockware.pma.connector.mapping.providers.internal;

import de.rockware.pma.connector.mapping.beans.Mapping;
import de.rockware.pma.connector.mapping.providers.MappingsProvider;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalMappingsProvider implements MappingsProvider {

  @Override
  public Collection<Mapping> create() {
    return IntStream.rangeClosed(1, 5)
        .mapToObj(
            i -> new Mapping(i, String.format("Field %d", i), String.format("Field %d M", i), 10))
        .collect(Collectors.toList());
  }
}
