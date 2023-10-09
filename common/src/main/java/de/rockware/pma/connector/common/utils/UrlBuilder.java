package de.rockware.pma.connector.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;

public class UrlBuilder {

  public static final String SLASH = "/";

  public static String append(String baseUrl, List<String> fragments, List<String> parameters) {
    if (StringUtils.isEmpty(baseUrl)) {
      return "";
    }
    List<String> urlFragments = new ArrayList<>();
    urlFragments.add(baseUrl);
    urlFragments.addAll(fragments);
    return appendFragments(urlFragments, parameters);
  }

  public static String appendFragments(List<String> fragments, List<String> parameters) {
    if (Objects.isNull(fragments) || fragments.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (String fragment : fragments) {
      if (StringUtils.isEmpty(fragment)) {
        continue;
      }
      fragment = removeEndingSlash(fragment);
      boolean addStartingSlash =
          builder.length() > 0 && !builder.substring(builder.toString().length() - 1).equals(SLASH);
      builder.append(addStartingSlash ? addStartingSlash(fragment) : fragment);
    }
    if (Objects.isNull(parameters) || parameters.isEmpty()) {
      return builder.toString();
    }
    StringBuilder parametersBuilder = new StringBuilder();
    for (String parameter : parameters) {
      if (parametersBuilder.length() > 0) {
        parametersBuilder.append("&");
      }
      parametersBuilder.append(parameter);
    }
    builder.append("?").append(parametersBuilder);
    return builder.toString();
  }

  private static String removeEndingSlash(String fragment) {
    if (StringUtils.isEmpty(fragment)) {
      return null;
    }
    if (fragment.endsWith(SLASH)) {
      return fragment.substring(0, fragment.length() - 1);
    }
    return fragment;
  }

  private static String addStartingSlash(String fragment) {
    if (StringUtils.isEmpty(fragment)) {
      return null;
    }
    if (!StringUtils.startsWith(fragment, SLASH)) {
      return String.format("/%s", fragment);
    }
    return fragment;
  }
}
