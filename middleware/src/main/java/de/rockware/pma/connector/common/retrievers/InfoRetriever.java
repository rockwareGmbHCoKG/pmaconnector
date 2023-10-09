package de.rockware.pma.connector.common.retrievers;

import de.rockware.pma.connector.execution.beans.Data;
import de.rockware.pma.connector.execution.beans.ExecutionError;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import java.util.*;
import java.util.stream.Collectors;

public class InfoRetriever {

  public static Collection<Info> getInfo(Data data) {
    Set<Info> result = new HashSet<>();
    if (Objects.isNull(data)) {
      return result;
    }
    result.addAll(getInfoFromRecords(data.getRecords()));
    result.addAll(getInfoFromErrors(data.getErrors()));
    return result;
  }

  public static Collection<Info> getInfoFromRecords(Collection<Record> records) {
    if (Objects.isNull(records)) {
      return Collections.emptySet();
    }
    return records.stream()
        .filter(r -> Objects.nonNull(r) && Objects.nonNull(r.getInfo()))
        .map(Record::getInfo)
        .collect(Collectors.toSet());
  }

  public static Collection<Info> getInfoFromErrors(Collection<ExecutionError> errors) {
    if (Objects.isNull(errors)) {
      return Collections.emptySet();
    }
    return errors.stream()
        .filter(r -> Objects.nonNull(r) && Objects.nonNull(r.getInfo()))
        .map(ExecutionError::getInfo)
        .collect(Collectors.toSet());
  }
}
