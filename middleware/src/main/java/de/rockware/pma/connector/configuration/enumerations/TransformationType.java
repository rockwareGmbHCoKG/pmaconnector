package de.rockware.pma.connector.configuration.enumerations;

import de.rockware.pma.connector.common.retrievers.EnumRetriever;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import de.rockware.pma.connector.transformation.transformers.internal.MappingTransformer;
import de.rockware.pma.connector.transformation.transformers.internal.UndefinedTransformer;
import java.util.function.Supplier;

public enum TransformationType {
  MAPPING(MappingTransformer::new),
  UNDEFINED(UndefinedTransformer::new);

  private final Supplier<Transformer> transformerFactory;

  TransformationType(Supplier<Transformer> transformerFactory) {
    this.transformerFactory = transformerFactory;
  }

  public Supplier<Transformer> getTransformerFactory() {
    return transformerFactory;
  }

  public static TransformationType getByValue(String value) {
    return EnumRetriever.getByValue(value, values(), TransformationType.UNDEFINED);
  }
}
