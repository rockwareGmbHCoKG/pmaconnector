package de.rockware.pma.connector.execution.beans;

import de.rockware.pma.connector.configuration.entities.ConfigurationValue;
import de.rockware.pma.connector.execution.entities.ExecutionStatusId;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExecutionContext {
  private final Collection<Mapping> mappings;
  private final Collection<ConfigurationValue> configurationValues;
  private final List<Info> initialInfoList = new ArrayList<>();
  private final Map<ExecutionStatusId, String> detailIdsCache = new HashMap<>();
  private final Data extractedData = new Data();
  private final Data transformedData = new Data();
  private final Data loadedData = new Data();
  private final Date startTime;

  public ExecutionContext(
      Collection<Mapping> mappings,
      Collection<ConfigurationValue> configurationValue,
      Date startTime) {
    this.mappings = mappings;
    this.configurationValues = configurationValue;
    this.startTime = startTime;
  }

  public boolean isEmpty() {
    return extractedData.isEmpty() && transformedData.isEmpty() && loadedData.isEmpty();
  }
}
