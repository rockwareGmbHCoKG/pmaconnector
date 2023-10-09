package de.rockware.pma.connector.common.retrievers;

import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class MappingRetriever {
  public static Mapping getBySourceField(Collection<Mapping> mappings, String sourceField) {
    if (Objects.isNull(mappings) || mappings.isEmpty() || StringUtils.isEmpty(sourceField)) {
      log.debug(
          String.format(
              "Invalid parameters: mappings=%s, sourceField=%s, returning null",
              mappings, sourceField));
      return null;
    }
    return mappings.stream()
        .filter(m -> m.getSource().equals(sourceField))
        .findFirst()
        .orElse(null);
  }
}
