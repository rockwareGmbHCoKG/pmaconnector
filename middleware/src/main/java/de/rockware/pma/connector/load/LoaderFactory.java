package de.rockware.pma.connector.load;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.factories.StepFactory;
import de.rockware.pma.connector.configuration.enumerations.ConfigurationKey;
import de.rockware.pma.connector.configuration.enumerations.LoadType;
import de.rockware.pma.connector.configuration.enumerations.LoadType.InstantiationContext;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.services.ExecutionStatusService;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.load.loaders.internal.UndefinedLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoaderFactory implements Factory<Loader, ExecutionContext> {

  private final RequestExecutor requestExecutor;
  private final ExecutionStatusService executionStatusService;
  private final TransferredRecipientsService transferredRecipientsService;

  @Autowired
  public LoaderFactory(
      RequestExecutor requestExecutor,
      ExecutionStatusService executionStatusService,
      TransferredRecipientsService transferredRecipientsService) {
    this.requestExecutor = requestExecutor;
    this.executionStatusService = executionStatusService;
    this.transferredRecipientsService = transferredRecipientsService;
  }

  @Override
  public Loader create(ExecutionContext executionContext) {
    return StepFactory.create(
        Step.LOAD,
        executionContext,
        ConfigurationKey.LOAD_TYPE,
        v ->
            LoadType.getByValue(v)
                .getLoaderFactory()
                .apply(
                    new InstantiationContext(
                        requestExecutor, executionStatusService, transferredRecipientsService)),
        new UndefinedLoader());
  }
}
