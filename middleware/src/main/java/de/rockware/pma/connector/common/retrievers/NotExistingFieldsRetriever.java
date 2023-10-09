package de.rockware.pma.connector.common.retrievers;

import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NotExistingFieldsRetriever {

  public static Collection<String> get(Collection<Mapping> mappings, List<String> currentFields) {
    if (Objects.isNull(mappings) || mappings.isEmpty() || Objects.isNull(currentFields)) {
      log.debug(
          String.format(
              "Invalid parameters: mappings=%s, currentFields=%s, returning null",
              mappings, currentFields));
      return null;
    }
    List<String> sourceFields =
        mappings.stream().map(Mapping::getSource).collect(Collectors.toList());
    return sourceFields.stream()
        .filter(n -> !currentFields.contains(n))
        .collect(Collectors.toSet());
  }
}
