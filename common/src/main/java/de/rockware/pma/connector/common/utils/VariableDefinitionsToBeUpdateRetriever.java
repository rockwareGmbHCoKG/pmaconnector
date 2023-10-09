package de.rockware.pma.connector.common.utils;

import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.VariableDefinitionElement;
import de.rockware.pma.connector.common.beans.VariableDefinition;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class VariableDefinitionsToBeUpdateRetriever {

  public static List<VariableDefinition> retrieve(
      List<VariableDefinitionElement> current, List<VariableDefinition> variableDefinitions) {
    if (Objects.isNull(current) || Objects.isNull(variableDefinitions)) {
      return null;
    }
    List<VariableDefinition> currentVariableDefinitions =
        current.stream()
            .filter(v -> Objects.nonNull(v) && Objects.nonNull(v.getDataType()))
            .map(
                v ->
                    new VariableDefinition(
                        v.getId(), v.getLabel(), v.getSortOrder(), v.getDataType().getId()))
            .collect(Collectors.toList());
    List<VariableDefinition> added =
        findNotExisting(variableDefinitions, currentVariableDefinitions);
    List<VariableDefinition> deleted =
        findNotExisting(currentVariableDefinitions, variableDefinitions);
    List<VariableDefinition> updated = findUpdated(currentVariableDefinitions, variableDefinitions);
    if (added.isEmpty() && deleted.isEmpty() && updated.isEmpty()) {
      return null;
    }
    HashSet<VariableDefinition> result = new HashSet<>(currentVariableDefinitions);
    result.addAll(added);
    for (VariableDefinition deletedOne : deleted) {
      VariableDefinition oldOne =
          result.stream().filter(c -> haveSameLabel(deletedOne, c)).findFirst().orElse(null);
      if (Objects.isNull(oldOne)) {
        continue;
      }
      result.remove(oldOne);
    }
    for (VariableDefinition updatedOne : updated) {
      VariableDefinition oldOne =
          result.stream().filter(c -> haveSameLabel(updatedOne, c)).findFirst().orElse(null);
      if (Objects.isNull(oldOne)) {
        continue;
      }
      oldOne.setDataTypeId(updatedOne.getDataTypeId());
    }
    for (VariableDefinition toBeUpdated : result) {
      VariableDefinition newOne =
          variableDefinitions.stream()
              .filter(v -> haveSameLabel(v, toBeUpdated))
              .findFirst()
              .orElse(null);
      if (Objects.isNull(newOne)) {
        continue;
      }
      toBeUpdated.setSortOrder(newOne.getSortOrder());
    }
    return result.stream()
        .sorted(Comparator.comparing(v -> Optional.ofNullable(v.getId()).orElse(0)))
        .collect(Collectors.toList());
  }

  private static boolean haveSameLabel(VariableDefinition first, VariableDefinition second) {
    return Objects.nonNull(first)
        && Objects.nonNull(second)
        && StringUtils.isNotEmpty(second.getLabel())
        && StringUtils.isNotEmpty(first.getLabel())
        && second.getLabel().equals(first.getLabel());
  }

  private static List<VariableDefinition> findNotExisting(
      List<VariableDefinition> toBeChecked, List<VariableDefinition> checkList) {
    return toBeChecked.stream()
        .filter(c -> checkList.stream().noneMatch(v -> haveSameLabel(v, c)))
        .collect(Collectors.toList());
  }

  private static List<VariableDefinition> findUpdated(
      List<VariableDefinition> toBeChecked, List<VariableDefinition> checkList) {
    List<VariableDefinition> result = new ArrayList<>();
    for (VariableDefinition variableDefinition : toBeChecked) {
      if (Objects.isNull(variableDefinition.getId())) {
        continue;
      }
      VariableDefinition foundInCheckList =
          checkList.stream()
              .filter(c -> haveSameLabel(variableDefinition, c))
              .findFirst()
              .orElse(null);
      if (Objects.isNull(foundInCheckList)) {
        continue;
      }
      if (areTheSame(variableDefinition, foundInCheckList)) {
        continue;
      }
      result.add(foundInCheckList);
    }
    return result;
  }

  private static boolean areTheSame(
      VariableDefinition oldVariableDefinition, VariableDefinition newVariableDefinition) {
    return oldVariableDefinition.getLabel().equals(newVariableDefinition.getLabel())
        && oldVariableDefinition.getDataTypeId() == newVariableDefinition.getDataTypeId();
  }
}
