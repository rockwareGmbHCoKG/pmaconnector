package de.rockware.pma.connector.mapping.providers.internal;

import de.rockware.pma.connector.mapping.beans.Mapping;
import de.rockware.pma.connector.mapping.providers.MappingsProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PmaMappingsProvider implements MappingsProvider {

  @Override
  public Collection<Mapping> create() {
    List<Mapping> mappings = new ArrayList<>();
    mappings.add(new Mapping(1, "First name (Recipient)", "firstname", 10));
    mappings.add(new Mapping(2, "Last name (Recipient)", "lastname", 10));
    mappings.add(
        new Mapping(3, "Address 3 (Number and street) (Recipient/Location)", "street", 10));
    mappings.add(new Mapping(4, "Zip/Postcode (Recipient/Location)", "zip", 80));
    mappings.add(new Mapping(5, "City (Recipient/Location)", "city", 10));
    return mappings;
  }
}
