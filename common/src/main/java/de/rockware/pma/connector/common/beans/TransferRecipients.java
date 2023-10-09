package de.rockware.pma.connector.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TransferRecipients implements Serializable {
  private int campaignId;
  private String customerId;
  private List<Recipients> recipients;

  public TransferRecipients() {
    // For Jackson purposes
  }

  public TransferRecipients(int campaignId, String customerId, List<Recipients> recipients) {
    this.campaignId = campaignId;
    this.customerId = customerId;
    this.recipients = recipients;
  }

  @JsonIgnore
  public boolean isValid() {
    return Objects.nonNull(customerId)
        && !recipients.isEmpty()
        && recipients.stream().allMatch(Recipients::isValid);
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class Recipients implements Serializable {
    private List<RecipientData> recipientData;
    private Integer recipientIdExt;

    public Recipients() {
      // For Jackson purposes
    }

    public Recipients(List<RecipientData> recipientData, Integer recipientIdExt) {
      this.recipientData = recipientData;
      this.recipientIdExt = recipientIdExt;
    }

    @JsonIgnore
    public boolean isValid() {
      return Objects.nonNull(recipientData)
          && !recipientData.isEmpty()
          && recipientData.stream().allMatch(RecipientData::isValid);
    }
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  @ToString
  public static class RecipientData implements Serializable {
    private String label;
    private String value;

    public RecipientData() {
      // For Jackson purposes
    }

    public RecipientData(String label, String value) {
      this.label = label;
      this.value = value;
    }

    @JsonIgnore
    public boolean isValid() {
      return StringUtils.isNotEmpty(label) && StringUtils.isNotEmpty(value);
    }
  }
}
