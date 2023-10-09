package de.rockware.pma.connector.load.loaders.stages;

import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import java.util.List;
import java.util.function.Consumer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public interface LoadingStage {

  void execute();

  default Consumer<HttpHeaders> fillHeaders(String token) {
    return h -> {
      h.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
      h.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      h.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    };
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  class Context {
    private ExecutionContext executionContext;
    private WebClient webClient;
    private String jwtToken;
    private String customerId;
    private Info info;
    private List<Record> records;
  }
}
