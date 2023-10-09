package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class MailingVariableDefinitionsCreationResult implements Serializable {
  private List<VariableDefinitionElement> elements = new ArrayList<>();
  private Page page;
  private String[] links;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class VariableDefinitionElement implements Serializable {
    private int id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date changedOn;

    private int version;
    private String label;
    private int sortOrder;
    private DataType dataType;
    private Integer addressVariableId;
    private boolean addressVariableMappingConfirmed;
    private boolean selected;
    private String x;
    private String y;
    private String font;
    private String fontColor;
    private Integer fontSize;
    private Integer spanHeight;

    public VariableDefinitionElement() {
      // for Jackson purposes
    }

    public VariableDefinitionElement(int id, String label, int dataTypeId, int sortOrder) {
      this.id = id;
      this.sortOrder = sortOrder;
      this.createdOn = new Date();
      this.version = 1;
      this.label = label;
      this.dataType = new DataType(dataTypeId);
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class DataType implements Serializable {
    private int id;
    private String label;

    public DataType() {
      // for Jackson purposes
    }

    public DataType(int id) {
      this.id = id;
      this.label = id == 10 ? "Text" : "Other";
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class Page implements Serializable {
    private int size;
    private int totalElements;
    private int totalPages;
    private int number;

    public Page() {
      // for Jackson purposes
    }

    public Page(int size, int totalElements, int totalPages, int number) {
      this.size = size;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
      this.number = number;
    }
  }
}
