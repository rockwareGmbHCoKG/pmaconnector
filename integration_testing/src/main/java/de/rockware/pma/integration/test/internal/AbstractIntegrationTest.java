package de.rockware.pma.integration.test.internal;

import de.rockware.pma.integration.beans.Environment;
import de.rockware.pma.integration.enumerations.ScriptCollection;
import de.rockware.pma.integration.status.TestStatus;
import de.rockware.pma.integration.test.IntegrationTest;
import de.rockware.pma.integration.utils.ResultWriter;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

abstract class AbstractIntegrationTest implements IntegrationTest {
  RestTemplate restTemplate = new RestTemplate();
  Runtime runtime = Runtime.getRuntime();

  @Override
  public void run(Environment env) {
    String testName = getClass().getSimpleName();
    try {
      ResultWriter.write(String.format("%s: Start\n", testName));
      execute(env);
      ResultWriter.write("Success!\n");
    } catch (Throwable e) {
      TestStatus.getInstance().getFailedTests().add(testName);
      ResultWriter.write(ExceptionUtils.getStackTrace(e));
    } finally {
      ResultWriter.write(String.format("%s: End\n\n", testName));
    }
  }

  abstract void execute(Environment env) throws Throwable;

  void cleanUp(Environment env) {
    // Cleanup middleware status tables
    get(
        buildUrl(env.getHostIp(), env.getPmaConnectorPort(), "/service/execution/status/clear"),
        null,
        env.getEncodedUserAndPassword(),
        String.class);
    get(
        buildUrl(
            env.getHostIp(), env.getPmaConnectorPort(), "/service/execution/status/details/clear"),
        null,
        env.getEncodedUserAndPassword(),
        String.class);
    get(
        buildUrl(
            env.getHostIp(), env.getPmaConnectorPort(), "/service/execution/status/error/clear"),
        null,
        env.getEncodedUserAndPassword(),
        String.class);
    get(
        buildUrl(
            env.getHostIp(),
            env.getPmaConnectorPort(),
            "/service/execution/status/transferred-recipients/clear"),
        null,
        env.getEncodedUserAndPassword(),
        String.class);
    // Clear caches in rest stub
    post(
        buildUrl(env.getHostIp(), env.getRestStubPort(), "/automation/clearcaches"),
        null,
        null,
        String.class);
  }

  <T> T get(String url, String body, String encodedUserAndPassword, Class<T> type) {
    return execute(
        e -> restTemplate.exchange(url, HttpMethod.GET, e, type),
        url,
        body,
        encodedUserAndPassword);
  }

  <T> T post(String url, String body, String encodedUserAndPassword, Class<T> type) {
    return execute(
        e -> restTemplate.postForEntity(url, e, type), url, body, encodedUserAndPassword);
  }

  <T> T execute(
      Function<HttpEntity<String>, ResponseEntity<T>> responseEntityFactory,
      String url,
      String body,
      String encodedUserAndPassword) {
    HttpHeaders headers = new HttpHeaders();
    headers.put("Content-Type", Collections.singletonList("application/json"));
    if (StringUtils.isNotEmpty(encodedUserAndPassword)) {
      headers.put(
          "Authorization",
          Collections.singletonList(String.format("Basic %s", encodedUserAndPassword)));
    }
    HttpEntity<String> entity = new HttpEntity<>(body, headers);
    ResponseEntity<T> response = responseEntityFactory.apply(entity);
    checkResponse(response, url);
    return response.getBody();
  }

  <T> void checkResponse(ResponseEntity<T> response, String url) {
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException(
          String.format(
              "Error executing %s, response status: %s, message: %s",
              url, response.getStatusCode(), response.getBody()));
    }
  }

  void assertTrue(Supplier<Boolean> test, String message) {
    if (!test.get()) {
      throw new RuntimeException(String.format("Test error: %s", message));
    }
  }

  void assertEquals(Object expected, Object obj) {
    try {
      if (!expected.equals(obj)) {
        throw new RuntimeException(
            String.format("Failure: expected:\n%s\nobj:\n%s", expected, obj));
      }
    } catch (Throwable e) {
      throw new RuntimeException(String.format("Failure: expected:\n%s\nobj:\n%s", expected, obj));
    }
  }

  ScriptCollection getScriptCollection() {
    String osName = System.getProperty("os.name");
    if (StringUtils.containsIgnoreCase(osName, "windows")) {
      return ScriptCollection.WINDOWS;
    }
    return ScriptCollection.UNIX;
  }

  String buildUrl(String ip, String port, String urlSuffix) {
    StringBuilder builder = new StringBuilder("http://");
    builder.append(ip);
    if (Objects.nonNull(port)) {
      builder.append(":").append(port);
    }
    if (StringUtils.isNotEmpty(urlSuffix)) {
      if (!urlSuffix.startsWith("/")) {
        builder.append("/");
      }
      builder.append(urlSuffix);
    }
    return builder.toString();
  }
}
