package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.VariableDefinitionElement;
import de.rockware.pma.connector.common.beans.VariableDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class VariableDefinitionsToBeUpdateRetrieverTest {

  @Test
  void nullCollections() {
    assertNull(VariableDefinitionsToBeUpdateRetriever.retrieve(null, null));
  }

  @Test
  void emptyCollections() {
    assertNull(
        VariableDefinitionsToBeUpdateRetriever.retrieve(
            Collections.emptyList(), Collections.emptyList()));
  }

  @Test
  void noNewVariableDefinitions() {
    List<VariableDefinitionElement> currentVariableDefinitions = new ArrayList<>();
    currentVariableDefinitions.add(new VariableDefinitionElement(1, "First", 10, 1));
    currentVariableDefinitions.add(new VariableDefinitionElement(2, "Second", 20, 2));
    currentVariableDefinitions.add(new VariableDefinitionElement(3, "Third", 30, 3));
    currentVariableDefinitions.add(new VariableDefinitionElement(4, "Fourth", 40, 4));
    currentVariableDefinitions.add(new VariableDefinitionElement(5, "Fifth", 50, 5));
    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    variableDefinitions.add(new VariableDefinition(null, "First", 1, 10));
    variableDefinitions.add(new VariableDefinition(null, "Second", 2, 20));
    variableDefinitions.add(new VariableDefinition(null, "Third", 3, 30));
    variableDefinitions.add(new VariableDefinition(null, "Fourth", 4, 40));
    variableDefinitions.add(new VariableDefinition(null, "Fifth", 5, 50));
    assertNull(
        VariableDefinitionsToBeUpdateRetriever.retrieve(
            currentVariableDefinitions, variableDefinitions));
  }

  @Test
  void differentSortOrders() {
    List<VariableDefinitionElement> currentVariableDefinitions = new ArrayList<>();
    currentVariableDefinitions.add(new VariableDefinitionElement(1, "First", 10, 1));
    currentVariableDefinitions.add(new VariableDefinitionElement(2, "Second", 20, 2));
    currentVariableDefinitions.add(new VariableDefinitionElement(3, "Third", 30, 3));
    currentVariableDefinitions.add(new VariableDefinitionElement(4, "Fourth", 40, 6));
    currentVariableDefinitions.add(new VariableDefinitionElement(5, "Fifth", 50, 7));
    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    variableDefinitions.add(new VariableDefinition(null, "First", 1, 10));
    variableDefinitions.add(new VariableDefinition(null, "Second", 2, 20));
    variableDefinitions.add(new VariableDefinition(null, "Third", 3, 30));
    variableDefinitions.add(new VariableDefinition(null, "Fourth", 4, 40));
    variableDefinitions.add(new VariableDefinition(null, "Fifth", 5, 50));
    assertNull(
        VariableDefinitionsToBeUpdateRetriever.retrieve(
            currentVariableDefinitions, variableDefinitions));
  }

  @Test
  void differents() {
    List<VariableDefinitionElement> currentVariableDefinitions = new ArrayList<>();
    currentVariableDefinitions.add(new VariableDefinitionElement(1, "First", 10, 1));
    currentVariableDefinitions.add(new VariableDefinitionElement(2, "Second", 20, 2));
    currentVariableDefinitions.add(new VariableDefinitionElement(3, "Third", 30, 3));
    currentVariableDefinitions.add(new VariableDefinitionElement(4, "Fourth", 40, 6));
    currentVariableDefinitions.add(new VariableDefinitionElement(5, "Fifth", 50, 7));
    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    variableDefinitions.add(new VariableDefinition(null, "First", 1, 11));
    variableDefinitions.add(new VariableDefinition(null, "Second", 2, 20));
    variableDefinitions.add(new VariableDefinition(null, "Third", 3, 30));
    variableDefinitions.add(new VariableDefinition(null, "Fourth", 4, 40));
    variableDefinitions.add(new VariableDefinition(null, "Sixth", 5, 60));
    assertEquals(
        Arrays.asList(
            new VariableDefinition(null, "Sixth", 5, 60),
            new VariableDefinition(1, "First", 1, 11),
            new VariableDefinition(2, "Second", 2, 20),
            new VariableDefinition(3, "Third", 3, 30),
            new VariableDefinition(4, "Fourth", 4, 40)),
        VariableDefinitionsToBeUpdateRetriever.retrieve(
            currentVariableDefinitions, variableDefinitions));
  }
}
