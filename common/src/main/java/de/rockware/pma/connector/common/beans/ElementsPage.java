package de.rockware.pma.connector.common.beans;

import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ElementsPage<T extends Serializable> implements Serializable {
  private List<T> elements;
  private PageInfo page;
  private List<Link> links;

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class PageInfo implements Serializable {
    private int size;
    private int totalElements;
    private int totalPages;
    private int number;

    public PageInfo() {
      // For Jackson purposes
    }

    public PageInfo(int size, int totalElements, int totalPages, int number) {
      this.size = size;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
      this.number = number;
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class Link implements Serializable {
    private String rel;
    private String href;

    public Link() {
      // For Jackson purposes
    }

    public Link(String rel, String href) {
      this.rel = rel;
      this.href = href;
    }
  }
}
