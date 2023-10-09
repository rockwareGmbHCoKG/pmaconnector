package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.common.beans.CreateMailingVariableDefinition;
import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult;
import de.rockware.pma.connector.common.beans.UpdateMailingVariableDefinition;
import de.rockware.pma.connector.common.beans.VariableDefinition;
import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.ConfigurationValueRetriever;
import de.rockware.pma.connector.common.updaters.DataUpdater;
import de.rockware.pma.connector.common.utils.VariableDefinitionsToBeUpdateRetriever;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.execution.beans.*;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.load.exceptions.LoadingStageException;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import de.rockware.pma.connector.load.loaders.stages.status.PmaStatusChecker;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;

public class CreateVariableDefinitionsLoadingStage implements LoadingStage {

  private final RequestExecutor requestExecutor;
  private final ExecutionContext executionContext;
  private final String customerId;
  private final Info info;

  public CreateVariableDefinitionsLoadingStage(
      RequestExecutor requestExecutor,
      ExecutionContext executionContext,
      String customerId,
      Info info) {
    this.requestExecutor = requestExecutor;
    this.executionContext = executionContext;
    this.customerId = customerId;
    this.info = info;
  }

  @Override
  public void execute() {
    List<VariableDefinition> variableDefinitions = new ArrayList<>();
    AtomicInteger counter = new AtomicInteger(0);
    for (Mapping mapping : executionContext.getMappings()) {
      variableDefinitions.add(
          new VariableDefinition(
              null, mapping.getTarget(), counter.getAndIncrement(), mapping.getType()));
    }
    addOptionalVariables(variableDefinitions, counter);
    MailingVariableDefinitionsCreationResult alreadyExisting =
        requestExecutor.getCreatedVariableDefinition(
            executionContext.getConfigurationValues(), info.getCreatedDeliveryId(), customerId);
    if (!PmaStatusChecker.isCampaignEditable(info) && Objects.isNull(alreadyExisting)) {
      throw new LoadingStageException(
          String.format(
              "Variable definitions does not exist for not editable campaign %s: execution aborted",
              CampaignDescriptionProvider.get(info)));
    }
    if (Objects.isNull(alreadyExisting)
        || Objects.isNull(alreadyExisting.getElements())
        || alreadyExisting.getElements().isEmpty()) {
      MailingVariableDefinitionsCreationResult createdVariableDefinitions =
          requestExecutor.createVariableDefinitions(
              executionContext.getConfigurationValues(),
              info.getCreatedDeliveryId(),
              new CreateMailingVariableDefinition(customerId, variableDefinitions));
      fieldsDefinitionsUpdated(
          Objects.isNull(createdVariableDefinitions),
          "Fields definitions successfully created on PMA environment");
    } else {
      checkVariableDefinitions(alreadyExisting, variableDefinitions);
    }
  }

  private void addOptionalVariables(
      List<VariableDefinition> variableDefinitions, AtomicInteger counter) {
    Data transformedData = executionContext.getTransformedData();
    if (Objects.isNull(transformedData) || transformedData.isEmpty()) {
      return;
    }
    for (Record record : transformedData.getRecords()) {
      for (Value value : record.getValues()) {
        if (Objects.isNull(value) || StringUtils.isEmpty(value.getField())) {
          continue;
        }
        if (variableDefinitions.stream().noneMatch(v -> v.getLabel().equals(value.getField()))) {
          variableDefinitions.add(
              new VariableDefinition(
                  null,
                  value.getField(),
                  counter.getAndIncrement(),
                  ConfigurationValueRetriever.getAsInt(
                      executionContext.getConfigurationValues(),
                      ConfigurationKey.OPTIONAL_MAPPINGS_TYPE)));
        }
      }
    }
  }

  private void checkVariableDefinitions(
      MailingVariableDefinitionsCreationResult createdVariableDefinitions,
      List<VariableDefinition> variableDefinitions) {
    List<VariableDefinition> toBeUpdated =
        VariableDefinitionsToBeUpdateRetriever.retrieve(
            createdVariableDefinitions.getElements(), variableDefinitions);
    if (Objects.isNull(toBeUpdated)) {
      fieldsDefinitionsUpdated(false, "Fields definitions already defined on PMA environment");
      return;
    }
    if (!info.isCampaignEditable()) {
      throw new LoadingStageException(
          String.format(
              "Campaign %s is not editable: impossible to update variable definitions",
              info.getCreatedCampaignId()));
    }
    UpdateMailingVariableDefinition updateMailingVariableDefinition =
        new UpdateMailingVariableDefinition(customerId, toBeUpdated);
    MailingVariableDefinitionsCreationResult updatedVariableDefinitions =
        requestExecutor.updateVariableDefinitions(
            executionContext.getConfigurationValues(),
            info.getCreatedDeliveryId(),
            updateMailingVariableDefinition);
    fieldsDefinitionsUpdated(
        Objects.isNull(updatedVariableDefinitions),
        "Fields definitions updated on PMA environment");
  }

  private void fieldsDefinitionsUpdated(boolean skip, String message) {
    if (skip) {
      return;
    }
    info.setFieldsDefinitionDone(true);
    DataUpdater.addMessage(executionContext, Step.LOAD, info, message);
  }
}
