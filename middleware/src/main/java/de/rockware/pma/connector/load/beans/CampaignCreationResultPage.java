package de.rockware.pma.connector.load.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.rockware.pma.connector.common.beans.CampaignCreationResult;
import de.rockware.pma.connector.common.beans.ElementsPage;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CampaignCreationResultPage extends ElementsPage<CampaignCreationResult> {

  @JsonIgnore
  public boolean isEmpty() {
    return Objects.isNull(getElements()) || getElements().isEmpty();
  }
}
