package de.rockware.pma.connector.configuration.enumerations;

import de.rockware.pma.connector.common.retrievers.EnumRetriever;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.internal.PmaRestLoader;
import de.rockware.pma.connector.load.loaders.internal.UndefinedLoader;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public enum LoadType {
  PMA_REST(
      c ->
          new PmaRestLoader(
              c.getRequestExecutor(),
              c.getExecutionStatusService(),
              c.getTransferredRecipientsService())),
  UNDEFINED(c -> new UndefinedLoader());

  private final Function<InstantiationContext, Loader> loaderFactory;

  LoadType(Function<InstantiationContext, Loader> loaderFactory) {
    this.loaderFactory = loaderFactory;
  }

  public Function<InstantiationContext, Loader> getLoaderFactory() {
    return loaderFactory;
  }

  public static LoadType getByValue(String value) {
    return EnumRetriever.getByValue(value, values(), LoadType.UNDEFINED);
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  public static class InstantiationContext {
    private final RequestExecutor requestExecutor;
    private final ExecutionStatusService executionStatusService;
    private final TransferredRecipientsService transferredRecipientsService;

    public InstantiationContext(
        RequestExecutor requestExecutor,
        ExecutionStatusService executionStatusService,
        TransferredRecipientsService transferredRecipientsService) {
      this.requestExecutor = requestExecutor;
      this.executionStatusService = executionStatusService;
      this.transferredRecipientsService = transferredRecipientsService;
    }
  }
}
