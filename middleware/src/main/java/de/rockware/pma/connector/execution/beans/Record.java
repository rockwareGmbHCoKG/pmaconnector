package de.rockware.pma.connector.execution.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Record {
  private final Info info;
  private final List<String> messages = new ArrayList<>();
  private final Collection<Value> values = new ArrayList<>();

  public Record(Info info) {
    this.info = info;
  }
}
