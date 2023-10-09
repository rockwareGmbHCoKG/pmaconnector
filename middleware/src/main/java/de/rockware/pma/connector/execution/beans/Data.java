package de.rockware.pma.connector.execution.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Data {
  private Collection<Record> records = new ArrayList<>();
  private List<ExecutionError> errors = new ArrayList<>();

  public boolean isEmpty() {
    return records.isEmpty() && errors.isEmpty();
  }
}
