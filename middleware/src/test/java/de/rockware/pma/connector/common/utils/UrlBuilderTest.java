package de.rockware.pma.connector.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class UrlBuilderTest {
  private static final String BASE_URL = "http://localhost:8080";

  @Test
  void nullBaseUrlAndFragments() {
    assertEquals("", UrlBuilder.append(null, null, null));
  }

  @Test
  void nullFragments() {
    assertEquals("", UrlBuilder.appendFragments(null, null));
  }

  @Test
  void nullBaseUrlAndSingleNullFragment() {
    assertEquals("", UrlBuilder.append(null, null, null));
  }

  @Test
  void singleNullFragment() {
    assertEquals(BASE_URL, UrlBuilder.append(BASE_URL, Collections.singletonList(null), null));
  }

  @Test
  void emptyBaseUrlAndFragments() {
    assertEquals("", UrlBuilder.append("", Collections.emptyList(), null));
  }

  @Test
  void emptyBaseUrlAndFragmentsWithParameters() {
    assertEquals(
        "",
        UrlBuilder.append(
            "", Collections.emptyList(), Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void emptyFragmentsAndParameters() {
    assertEquals(
        BASE_URL, UrlBuilder.append(BASE_URL, Collections.emptyList(), Collections.emptyList()));
  }

  @Test
  void emptyFragments() {
    assertEquals(
        "http://localhost:8080?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL, Collections.emptyList(), Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void emptyParameters() {
    assertEquals(BASE_URL, UrlBuilder.append(BASE_URL, Collections.emptyList(), null));
  }

  @Test
  void nullBaseUrlAndFragmentsInArray() {
    assertEquals("", UrlBuilder.append(null, null, null));
  }

  @Test
  void singleFragmentWithSlash() {
    assertEquals(
        "http://localhost:8080/fragment",
        UrlBuilder.append(BASE_URL, Collections.singletonList("/fragment"), null));
  }

  @Test
  void singleFragmentWithSlashAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Collections.singletonList("/fragment"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void baseUrlWithSlashAndSingleFragmentWithSlash() {
    assertEquals(
        "http://localhost:8080/fragment",
        UrlBuilder.append(BASE_URL + "/", Collections.singletonList("/fragment"), null));
  }

  @Test
  void baseUrlWithSlashAndSingleFragmentWithSlashAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL + "/",
            Collections.singletonList("/fragment"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void singleFragmentWithoutSlash() {
    assertEquals(
        "http://localhost:8080/fragment",
        UrlBuilder.append(BASE_URL, Collections.singletonList("fragment"), null));
  }

  @Test
  void singleFragmentWithoutSlashAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Collections.singletonList("fragment"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void baseUrlWithSlashAndSingleFragmentWithoutSlash() {
    assertEquals(
        "http://localhost:8080/fragment",
        UrlBuilder.append(BASE_URL + "/", Collections.singletonList("fragment"), null));
  }

  @Test
  void baseUrlWithSlashAndSingleFragmentWithoutSlashAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL + "/",
            Collections.singletonList("fragment"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void singleFragmentWithEndSlash() {
    assertEquals(
        "http://localhost:8080/fragment",
        UrlBuilder.append(BASE_URL, Collections.singletonList("fragment/"), null));
  }

  @Test
  void singleFragmentWithEndSlashAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Collections.singletonList("fragment/"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void multipleFragmentsWithSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(BASE_URL, Arrays.asList("/fragment1", "/fragment2", "/fragment3"), null));
  }

  @Test
  void multipleFragmentsWithSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Arrays.asList("/fragment1", "/fragment2", "/fragment3"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(
            BASE_URL + "/", Arrays.asList("/fragment1", "/fragment2", "/fragment3"), null));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL + "/",
            Arrays.asList("/fragment1", "/fragment2", "/fragment3"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void multipleFragmentsWithoutSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(BASE_URL, Arrays.asList("fragment1", "fragment2", "fragment3"), null));
  }

  @Test
  void multipleFragmentsWithoutSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Arrays.asList("fragment1", "fragment2", "fragment3"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithoutSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(
            BASE_URL + "/", Arrays.asList("fragment1", "fragment2", "fragment3"), null));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithoutSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL + "/",
            Arrays.asList("fragment1", "fragment2", "fragment3"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void multipleFragmentsWithStartAndEndSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(
            BASE_URL, Arrays.asList("fragment1/", "/fragment2/", "/fragment3/"), null));
  }

  @Test
  void multipleFragmentsWithStartAndEndSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL,
            Arrays.asList("fragment1/", "/fragment2/", "/fragment3/"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithStartAndEndSlashes() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3",
        UrlBuilder.append(
            BASE_URL + "/", Arrays.asList("fragment1/", "/fragment2/", "/fragment3/"), null));
  }

  @Test
  void baseUrlWithSlashAndMultipleFragmentsWithStartAndEndSlashesAndParameters() {
    assertEquals(
        "http://localhost:8080/fragment1/fragment2/fragment3?param1=1&param2=2&param3=3",
        UrlBuilder.append(
            BASE_URL + "/",
            Arrays.asList("fragment1/", "/fragment2/", "/fragment3/"),
            Arrays.asList("param1=1", "param2=2", "param3=3")));
  }
}
