package de.rockware.pma.connector.execution.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AddValuesRequestBody<T> implements Serializable {
  private T[] values;
}
