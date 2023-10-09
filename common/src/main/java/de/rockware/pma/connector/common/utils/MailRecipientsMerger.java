package de.rockware.pma.connector.common.utils;

import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

@Log4j2
public class MailRecipientsMerger {

  public static String merge(String recipients, List<String> additionalRecipients) {
    Set<String> allRecipients = new HashSet<>(split(recipients));
    for (String additionalRecipient :
        Optional.ofNullable(additionalRecipients).orElse(Collections.emptyList())) {
      allRecipients.addAll(split(additionalRecipient));
    }
    if (allRecipients.isEmpty()) {
      return null;
    }
    return allRecipients.stream().sorted().reduce((a1, a2) -> a1 + "," + a2).orElse(null);
  }

  private static List<String> split(String recipients) {
    if (StringUtils.isEmpty(recipients)) {
      return Collections.emptyList();
    }
    try {
      List<String> trimmedRecipients = new ArrayList<>();
      String[] splittedRecipients = recipients.split(",");
      for (String recipient : splittedRecipients) {
        trimmedRecipients.add(recipient.trim());
      }
      return trimmedRecipients;
    } catch (Throwable e) {
      log.error(String.format("Error splitting recipients %s: %s", recipients, e.getMessage()), e);
      return Collections.emptyList();
    }
  }
}
