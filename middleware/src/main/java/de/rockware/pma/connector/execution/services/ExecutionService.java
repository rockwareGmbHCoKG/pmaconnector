package de.rockware.pma.connector.execution.services;

import de.rockware.pma.connector.execution.beans.Info;
import java.util.List;

public interface ExecutionService {

  void run();

  void run(List<Info> infoList, List<String> additionalMailRecipients);
}
