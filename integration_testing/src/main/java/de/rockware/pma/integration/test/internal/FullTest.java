package de.rockware.pma.integration.test.internal;

import static de.rockware.pma.integration.beans.ExecutionPage.*;

import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.integration.beans.Environment;
import de.rockware.pma.integration.beans.ExecutionDetailsPage;
import de.rockware.pma.integration.beans.ExecutionDetailsPage.ExecutionDetails;
import de.rockware.pma.integration.beans.ExecutionErrorsPage;
import de.rockware.pma.integration.beans.ExecutionPage;
import de.rockware.pma.integration.beans.Push;
import de.rockware.pma.integration.beans.StartCampaign;
import de.rockware.pma.integration.beans.TransferredRecipientsPage;
import de.rockware.pma.integration.beans.TransferredRecipientsPage.TransferredRecipients;
import de.rockware.pma.integration.enumerations.ScriptCollection;
import java.util.Objects;

public class FullTest extends AbstractIntegrationTest {
  @Override
  void execute(Environment env) throws Throwable {
    ScriptCollection scriptCollection = getScriptCollection();
    // Copy files in extraction folder
    runtime.exec(scriptCollection.getCopyFilesCommand());
    cleanUp(env);
    // Send proof
    post(
        buildUrl(env.getHostIp(), env.getPmaConnectorPort(), "/service/extraction/push"),
        BeanConverter.serialize(create(true)),
        env.getEncodedUserAndPassword(),
        String.class);
    // Mark campaign as started
    post(
        buildUrl(env.getHostIp(), env.getRestStubPort(), "/automation/startcampaign"),
        BeanConverter.serialize(new StartCampaign("42", "CAMPAIGN1_DELIVERY1")),
        env.getEncodedUserAndPassword(),
        String.class);
    // Restore files in extraction folder
    runtime.exec(scriptCollection.getCopyFilesCommand());
    Thread.sleep(1000);
    // Send recipients
    post(
        buildUrl(env.getHostIp(), env.getPmaConnectorPort(), "/service/extraction/push"),
        BeanConverter.serialize(create(false)),
        env.getEncodedUserAndPassword(),
        String.class);
    // Checks
    ExecutionPage execution =
        get(
            buildUrl(
                env.getHostIp(),
                env.getPmaConnectorPort(),
                "/service/execution/status/getPage?campaignId=CAMPAIGN1&page=0&size=10"),
            null,
            env.getEncodedUserAndPassword(),
            ExecutionPage.class);
    assertTrue(() -> Objects.nonNull(execution), "Execution status is null");
    assertTrue(() -> execution.getTotalElements() == 1, "No execution status elements exist");
    assertEquals(
        new Execution(
            new Id("CAMPAIGN1", "DELIVERY1"),
            "Campaign 1",
            "Delivery 1",
            true,
            false,
            true,
            true,
            true),
        execution.getContent().get(0));
    ExecutionDetailsPage executionDetails =
        get(
            buildUrl(
                env.getHostIp(),
                env.getPmaConnectorPort(),
                "/service/execution/status/details/getPage?campaignId=CAMPAIGN1&page=0&size=10"),
            null,
            env.getEncodedUserAndPassword(),
            ExecutionDetailsPage.class);
    assertTrue(() -> Objects.nonNull(executionDetails), "Execution status details are null");
    assertTrue(
        () -> executionDetails.getTotalElements() == 2,
        "No execution status details elements exist");
    ExecutionDetails executionDetailsValue = executionDetails.getContent().get(0);
    assertEquals("CAMPAIGN1", executionDetailsValue.getCampaignId());
    assertEquals("DELIVERY1", executionDetailsValue.getDeliveryId());
    assertTrue(
        () -> executionDetailsValue.getExtractionMessages().isEmpty(),
        "Extraction messages are not empty");
    assertTrue(
        () -> executionDetailsValue.getTransformationMessages().isEmpty(),
        "Transformation messages are not empty");
    assertTrue(
        () -> executionDetailsValue.getLoadingMessages().size() == 4,
        "Loading messages are not as expected");
    ExecutionErrorsPage executionErrors =
        get(
            buildUrl(
                env.getHostIp(),
                env.getPmaConnectorPort(),
                "/service/execution/status/error/getPage?campaignId=CAMPAIGN1&page=0&size=10"),
            null,
            env.getEncodedUserAndPassword(),
            ExecutionErrorsPage.class);
    assertTrue(() -> Objects.nonNull(executionErrors), "Execution status errors are null");
    assertTrue(() -> executionErrors.getTotalElements() == 0, "There are execution status errors");
    TransferredRecipientsPage transferredRecipients =
        get(
            buildUrl(
                env.getHostIp(),
                env.getPmaConnectorPort(),
                "/service/execution/status/transferred-recipients/getPage?campaignId=CAMPAIGN1&page=0&size=10"),
            null,
            env.getEncodedUserAndPassword(),
            TransferredRecipientsPage.class);
    assertTrue(() -> Objects.nonNull(transferredRecipients), "Transferred recipients are null");
    assertTrue(
        () -> transferredRecipients.getTotalElements() == 1,
        "There are a different amount of transferred recipients records");
    assertEquals(
        new TransferredRecipients("CAMPAIGN1", "DELIVERY1", 20),
        transferredRecipients.getContent().get(0));
  }

  private Push create(boolean proof) {
    return new Push(
        "DELIVERY1",
        "Delivery 1",
        "CAMPAIGN1",
        "Campaign 1",
        "CAMPAIGN1_Campaign 1_DELIVERY1_Delivery 1_2021-09-01_2021-12-31.csv",
        Boolean.toString(proof));
  }
}
