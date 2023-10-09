package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VariableDefinition implements Serializable {
  private Integer id;
  private String label;
  private int sortOrder;
  private int dataTypeId;

  public VariableDefinition() {
    // For Jackson purposes
  }

  public VariableDefinition(Integer id, String label, int sortOrder, int dataTypeId) {
    this.id = id;
    this.label = label;
    this.sortOrder = sortOrder;
    this.dataTypeId = dataTypeId;
  }

  @JsonIgnore
  public boolean isValid() {
    return StringUtils.isNotEmpty(label);
  }
}
