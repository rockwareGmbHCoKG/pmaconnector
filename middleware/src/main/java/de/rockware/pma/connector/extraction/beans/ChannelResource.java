package de.rockware.pma.connector.extraction.beans;

import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChannelResource {
  private String name;
  private InputStream inputStream;

  public ChannelResource() {}

  public ChannelResource(String name, InputStream inputStream) {
    this.inputStream = inputStream;
    this.name = name;
  }
}
