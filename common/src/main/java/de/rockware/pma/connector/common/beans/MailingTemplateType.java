package de.rockware.pma.connector.common.beans;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MailingTemplateType implements Serializable {
  private Integer mailingTemplateTypeId;
  private String editorType;
}
