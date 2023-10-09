package de.rockware.pma.connector.common.updaters;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.retrievers.DataRetriever;
import de.rockware.pma.connector.execution.beans.*;
import de.rockware.pma.connector.execution.beans.Record;
import java.util.List;
import java.util.Objects;

public class DataUpdater {

  public static void addRecord(ExecutionContext executionContext, Step step, Record record) {
    if (Objects.isNull(executionContext)) {
      return;
    }
    if (Objects.isNull(record)) {
      return;
    }
    Data data = DataRetriever.getData(executionContext, step);
    if (Objects.isNull(data)) {
      return;
    }
    data.getRecords().add(record);
  }

  public static void addMessage(
      ExecutionContext executionContext, Step step, Info info, String message) {
    if (Objects.isNull(executionContext)) {
      return;
    }
    if (Objects.isNull(info)) {
      return;
    }
    Data data = DataRetriever.getData(executionContext, step);
    if (Objects.isNull(data)) {
      return;
    }
    Record record =
        data.getRecords().stream().filter(r -> info.equals(r.getInfo())).findFirst().orElse(null);
    if (Objects.isNull(record)) {
      record = new Record(info);
      data.getRecords().add(record);
    }
    record.getMessages().add(message);
  }

  public static void addError(
      ExecutionContext executionContext,
      Info info,
      Step step,
      String message,
      Throwable throwable) {
    if (Objects.isNull(executionContext)) {
      return;
    }
    Data data = DataRetriever.getData(executionContext, step);
    if (Objects.isNull(data)) {
      return;
    }
    List<ExecutionError> errors = data.getErrors();
    ExecutionError executionError = new ExecutionError(info, step, null, message, throwable);
    if (!errors.contains(executionError)) {
      errors.add(executionError);
    }
  }
}
