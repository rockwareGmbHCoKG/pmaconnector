package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UpdateMailingVariableDefinition implements Serializable {
  private String customerId;
  private List<VariableDefinition> updateVariableDefRequestRepList;

  public UpdateMailingVariableDefinition() {
    // For Jackson purposes
  }

  public UpdateMailingVariableDefinition(
      String customerId, List<VariableDefinition> updateVariableDefRequestRepList) {
    this.customerId = customerId;
    this.updateVariableDefRequestRepList = updateVariableDefRequestRepList;
  }

  @JsonIgnore
  public boolean isValid() {
    return StringUtils.isNotEmpty(customerId)
        && Objects.nonNull(updateVariableDefRequestRepList)
        && updateVariableDefRequestRepList.stream().allMatch(VariableDefinition::isValid);
  }
}
