package de.rockware.pma.connector.common.converters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.rockware.pma.connector.common.beans.*;
import de.rockware.pma.connector.common.utils.OidGenerator;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class BeanConverterTest {

  @Test
  void serializeError() {
    assertThrows(RuntimeException.class, () -> BeanConverter.serialize(new NotSerializable()));
  }

  @Test
  void serializeListCollectionError() {
    assertThrows(
        RuntimeException.class,
        () -> BeanConverter.serializeCollection(Collections.singleton(new NotSerializable())));
  }

  @Test
  void deserializeError() {
    assertThrows(
        RuntimeException.class,
        () -> BeanConverter.deserialize("Random string", CampaignCreationResult.class));
  }

  @Test
  void deserialize_CampaignCreationResult() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_campaign_creation_result.json"))
                        .toURI())));
    CampaignCreationResult campaignCreationResult =
        BeanConverter.deserialize(value, CampaignCreationResult.class);
    assertNotNull(campaignCreationResult);
  }

  @Test
  void deserialize_AuthenticationBody() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_authentication_body.json"))
                        .toURI())));
    AuthenticationBody authenticationBody =
        BeanConverter.deserialize(value, AuthenticationBody.class);
    assertNotNull(authenticationBody);
  }

  @Test
  void deserialize_Mailing() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("sample_mailing.json"))
                        .toURI())));
    Mailing mailing = BeanConverter.deserialize(value, Mailing.class);
    assertNotNull(mailing);
  }

  @Test
  void deserialize_MailingCreationResult() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_mailing_creation_result.json"))
                        .toURI())));
    MailingCreationResult mailingCreationResult =
        BeanConverter.deserialize(value, MailingCreationResult.class);
    assertNotNull(mailingCreationResult);
  }

  @Test
  void deserialize_MailingVariableDefinition() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_mailing_variable_definition.json"))
                        .toURI())));
    CreateMailingVariableDefinition createMailingVariableDefinition =
        BeanConverter.deserialize(value, CreateMailingVariableDefinition.class);
    assertNotNull(createMailingVariableDefinition);
  }

  @Test
  void deserialize_MailingVariableDefinitionCreationResult() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource(
                                    "sample_mailing_variable_definition_creation_result.json"))
                        .toURI())));
    MailingVariableDefinitionsCreationResult mailingVariableDefinitionsCreationResult =
        BeanConverter.deserialize(value, MailingVariableDefinitionsCreationResult.class);
    assertNotNull(mailingVariableDefinitionsCreationResult);
  }

  @Test
  void deserialize_TransferRecipients() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_transfer_recipients.json"))
                        .toURI())));
    TransferRecipients transferRecipients =
        BeanConverter.deserialize(value, TransferRecipients.class);
    assertNotNull(transferRecipients);
  }

  @Test
  void deserialize_TransferRecipientsResult() throws Exception {
    String value =
        new String(
            Files.readAllBytes(
                Paths.get(
                    Objects.requireNonNull(
                            getClass()
                                .getClassLoader()
                                .getResource("sample_transfer_recipients_result.json"))
                        .toURI())));
    TransferRecipientsResult transferRecipientsResult =
        BeanConverter.deserialize(value, TransferRecipientsResult.class);
    assertNotNull(transferRecipientsResult);
  }

  @Getter
  @Setter
  private static class NotSerializable implements Serializable {
    private static String value = OidGenerator.generate();
  }
}
