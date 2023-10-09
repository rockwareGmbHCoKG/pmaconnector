package de.rockware.pma.connector.load.loaders.stages.internal;

import de.rockware.pma.connector.load.loaders.stages.LoadingStage;
import java.util.List;

public class CompositeLoadingStage implements LoadingStage {
  private final List<LoadingStage> loadingStages;

  public CompositeLoadingStage(List<LoadingStage> loadingStages) {
    this.loadingStages = loadingStages;
  }

  @Override
  public void execute() {
    for (LoadingStage loadingStage : loadingStages) {
      loadingStage.execute();
    }
  }
}
