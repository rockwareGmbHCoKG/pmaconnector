package de.rockware.pma.connector.transformation;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.factories.StepFactory;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.TransformationType;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import de.rockware.pma.connector.transformation.transformers.internal.UndefinedTransformer;
import org.springframework.stereotype.Component;

@Component
public class TransformerFactory implements Factory<Transformer, ExecutionContext> {

  @Override
  public Transformer create(ExecutionContext executionContext) {
    return StepFactory.create(
        Step.TRANSFORMATION,
        executionContext,
        ConfigurationKey.TRANSFORMATION_TYPE,
        v -> TransformationType.getByValue(v).getTransformerFactory().get(),
        new UndefinedTransformer());
  }
}
