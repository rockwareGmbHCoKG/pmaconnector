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
public class CreateMailingVariableDefinition implements Serializable {
  private String customerId;
  private List<VariableDefinition> createVariableDefRequestRepList;

  public CreateMailingVariableDefinition() {
    // For Jackson purposes
  }

  public CreateMailingVariableDefinition(
      String customerId, List<VariableDefinition> createVariableDefRequestRepList) {
    this.customerId = customerId;
    this.createVariableDefRequestRepList = createVariableDefRequestRepList;
  }

  @JsonIgnore
  public boolean isValid() {
    return StringUtils.isNotEmpty(customerId)
        && Objects.nonNull(createVariableDefRequestRepList)
        && createVariableDefRequestRepList.stream().allMatch(VariableDefinition::isValid);
  }
}
