package de.rockware.pma.connector.load.loaders.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createExecutionContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.beans.*;
import de.rockware.pma.connector.common.beans.ElementsPage.PageInfo;
import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.Page;
import de.rockware.pma.connector.common.beans.MailingVariableDefinitionsCreationResult.VariableDefinitionElement;
import de.rockware.pma.connector.common.converters.BeanConverter;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.beans.Value;
import de.rockware.pma.connector.execution.repositiories.ExecutionStatusRepository;
import de.rockware.pma.connector.execution.services.TransferredRecipientsService;
import de.rockware.pma.connector.execution.services.internal.ExecutionStatusServiceImpl;
import de.rockware.pma.connector.load.beans.CampaignCreationResultPage;
import de.rockware.pma.connector.load.beans.MailingCreationResultPage;
import de.rockware.pma.connector.load.exceptions.LoaderException;
import de.rockware.pma.connector.load.loaders.clients.RequestExecutor;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PmaRestLoaderTest {
  private static final String ALGORITHM_AND_TOKEN_TYPE = "{\"typ\":\"JWT\",\"alg\":\"HS512\"}";

  @Mock private RequestExecutor requestExecutor;
  @Mock private ExecutionStatusRepository executionStatusRepository;
  @Mock private TransferredRecipientsService transferredRecipientsService;

  private PmaRestLoader sut;

  @BeforeEach
  void setUp() {
    this.sut =
        new PmaRestLoader(
            requestExecutor,
            new ExecutionStatusServiceImpl(executionStatusRepository),
            transferredRecipientsService);
  }

  @Test
  void nullContext() {
    sut.load(null);
    verifyNoInteractions(requestExecutor, executionStatusRepository);
  }

  @Test
  void emptyContext() {
    sut.load(new ExecutionContext(null, null, null));
    verifyNoInteractions(requestExecutor, executionStatusRepository);
  }

  @Test
  void noData() {
    sut.load(createExecutionContext());
    verifyNoInteractions(requestExecutor, executionStatusRepository);
  }

  @Test
  void errorAuthentication() {
    when(requestExecutor.authenticate(anyCollection(), any()))
        .thenThrow(new RuntimeException("Test exception"));
    assertThrows(LoaderException.class, () -> sut.load(createValidExecutionContext(true)));
  }

  @Test
  void errorCreatingCampaign() {
    String token = createToken();
    when(requestExecutor.getCreatedCampaignsPage(anyCollection(), any())).thenReturn(null);
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    ExecutionContext executionContext = createValidExecutionContext(true);
    sut.load(executionContext);
    assertEquals(0, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 0));
    assertEquals(5, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void errorCreatingDelivery() {
    String token = createToken();
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    AtomicInteger campaignCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new CampaignCreationResult(
                    100000 + campaignCounter.getAndIncrement(),
                    i.getArgument(1, Campaign.class).getCampaignName(),
                    i.getArgument(1, Campaign.class).getCampaignIdExt()))
        .when(requestExecutor)
        .createCampaign(anyCollection(), any());
    when(requestExecutor.createDelivery(anyCollection(), any()))
        .thenThrow(new RuntimeException("Test exception"));
    ExecutionContext executionContext = createValidExecutionContext(true);
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && !i.isDeliveryCreated()
                        && Objects.isNull(i.getCreatedDeliveryId())
                        && !i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 1));
    assertEquals(5, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void errorCreatingVariableDefinitions() {
    String token = createToken();
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    AtomicInteger campaignCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new CampaignCreationResult(
                    100000 + campaignCounter.getAndIncrement(),
                    i.getArgument(1, Campaign.class).getCampaignName(),
                    i.getArgument(1, Campaign.class).getCampaignIdExt()))
        .when(requestExecutor)
        .createCampaign(anyCollection(), any());
    AtomicInteger mailingCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new MailingCreationResult(
                    150000 + mailingCounter.getAndIncrement(),
                    i.getArgument(1, Mailing.class).getCampaignId()))
        .when(requestExecutor)
        .createDelivery(anyCollection(), any());
    doThrow(new RuntimeException("Test exception!"))
        .when(requestExecutor)
        .createVariableDefinitions(anyCollection(), anyInt(), any());
    ExecutionContext executionContext = createValidExecutionContext(true);
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && i.isDeliveryCreated()
                        && Objects.nonNull(i.getCreatedDeliveryId())
                        && !i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 2));
    assertEquals(5, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void errorTransferringRecipients() {
    String token = createToken();
    ExecutionContext executionContext = createValidExecutionContext(false);
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    CampaignCreationResultPage campaignCreationResultPage = new CampaignCreationResultPage();
    campaignCreationResultPage.setElements(new ArrayList<>());
    for (int i = 0; i < 6; i++) {
      campaignCreationResultPage
          .getElements()
          .add(
              new CampaignCreationResult(
                  100000 + i,
                  String.format("Campaign %d - Delivery %d", i, i),
                  new CampaignState(120, "Active")));
    }
    campaignCreationResultPage.setPage(new PageInfo(5, 5, 1, 0));
    when(requestExecutor.getCreatedCampaignsPage(anyCollection(), any()))
        .thenReturn(campaignCreationResultPage);
    MailingCreationResultPage mailingCreationResultPage = new MailingCreationResultPage();
    mailingCreationResultPage.setElements(new ArrayList<>());
    for (int i = 0; i < 6; i++) {
      mailingCreationResultPage
          .getElements()
          .add(new MailingCreationResult(150000 + i, 100000 + i));
    }
    mailingCreationResultPage.setPage(new PageInfo(5, 5, 1, 0));
    when(requestExecutor.getCreatedDeliveryPage(anyCollection(), any(), anyInt()))
        .thenReturn(mailingCreationResultPage);
    doAnswer(
            i -> {
              MailingVariableDefinitionsCreationResult result =
                  new MailingVariableDefinitionsCreationResult();
              result.setElements(new ArrayList<>());
              Collection<Mapping> mappings = executionContext.getMappings();
              mappings.forEach(
                  m ->
                      result
                          .getElements()
                          .add(
                              new VariableDefinitionElement(
                                  (int) m.getId(), m.getTarget(), m.getType(), (int) m.getId())));
              result.setPage(new Page(mappings.size(), mappings.size(), 1, 0));
              return result;
            })
        .when(requestExecutor)
        .getCreatedVariableDefinition(anyCollection(), anyInt(), anyString());
    doThrow(new RuntimeException("Test exception!"))
        .when(requestExecutor)
        .transferRecipients(anyCollection(), any());
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && i.isDeliveryCreated()
                        && Objects.nonNull(i.getCreatedDeliveryId())
                        && i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 3));
    assertEquals(5, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void successProofOnly() {
    String token = createToken();
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    AtomicInteger campaignCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new CampaignCreationResult(
                    100000 + campaignCounter.getAndIncrement(),
                    i.getArgument(1, Campaign.class).getCampaignName(),
                    i.getArgument(1, Campaign.class).getCampaignIdExt()))
        .when(requestExecutor)
        .createCampaign(anyCollection(), any());
    AtomicInteger mailingCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new MailingCreationResult(
                    150000 + mailingCounter.getAndIncrement(),
                    i.getArgument(1, Mailing.class).getCampaignId()))
        .when(requestExecutor)
        .createDelivery(anyCollection(), any());
    doAnswer(i -> new MailingVariableDefinitionsCreationResult())
        .when(requestExecutor)
        .createVariableDefinitions(anyCollection(), anyInt(), any());
    ExecutionContext executionContext = createValidExecutionContext(true);
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && i.isDeliveryCreated()
                        && Objects.nonNull(i.getCreatedDeliveryId())
                        && i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 3));
    assertEquals(0, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void sendProofSuccess() {
    String token = createToken();
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    AtomicInteger campaignCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new CampaignCreationResult(
                    100000 + campaignCounter.getAndIncrement(),
                    i.getArgument(1, Campaign.class).getCampaignName(),
                    i.getArgument(1, Campaign.class).getCampaignIdExt()))
        .when(requestExecutor)
        .createCampaign(anyCollection(), any());
    AtomicInteger mailingCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new MailingCreationResult(
                    150000 + mailingCounter.getAndIncrement(),
                    i.getArgument(1, Mailing.class).getCampaignId()))
        .when(requestExecutor)
        .createDelivery(anyCollection(), any());
    doAnswer(i -> new MailingVariableDefinitionsCreationResult())
        .when(requestExecutor)
        .createVariableDefinitions(anyCollection(), anyInt(), any());
    ExecutionContext executionContext = createValidExecutionContext(true);
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && i.isDeliveryCreated()
                        && Objects.nonNull(i.getCreatedDeliveryId())
                        && i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 3));
    assertEquals(0, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void transferRecipientsSuccess() {
    String token = createToken();
    ExecutionContext executionContext = createValidExecutionContext(false);
    when(requestExecutor.authenticate(anyCollection(), any())).thenReturn(token);
    CampaignCreationResultPage campaignCreationResultPage = new CampaignCreationResultPage();
    campaignCreationResultPage.setElements(new ArrayList<>());
    for (int i = 0; i < 6; i++) {
      campaignCreationResultPage
          .getElements()
          .add(
              new CampaignCreationResult(
                  100000 + i,
                  String.format("Campaign %d - Delivery %d", i, i),
                  new CampaignState(120, "Active")));
    }
    campaignCreationResultPage.setPage(new PageInfo(5, 5, 1, 0));
    when(requestExecutor.getCreatedCampaignsPage(anyCollection(), any()))
        .thenReturn(campaignCreationResultPage);
    MailingCreationResultPage mailingCreationResultPage = new MailingCreationResultPage();
    mailingCreationResultPage.setElements(new ArrayList<>());
    for (int i = 0; i < 6; i++) {
      mailingCreationResultPage
          .getElements()
          .add(new MailingCreationResult(150000 + i, 100000 + i));
    }
    mailingCreationResultPage.setPage(new PageInfo(5, 5, 1, 0));
    when(requestExecutor.getCreatedDeliveryPage(anyCollection(), any(), anyInt()))
        .thenReturn(mailingCreationResultPage);
    doAnswer(
            i -> {
              MailingVariableDefinitionsCreationResult result =
                  new MailingVariableDefinitionsCreationResult();
              result.setElements(new ArrayList<>());
              Collection<Mapping> mappings = executionContext.getMappings();
              mappings.forEach(
                  m ->
                      result
                          .getElements()
                          .add(
                              new VariableDefinitionElement(
                                  (int) m.getId(), m.getTarget(), m.getType(), (int) m.getId())));
              result.setPage(new Page(mappings.size(), mappings.size(), 1, 0));
              return result;
            })
        .when(requestExecutor)
        .getCreatedVariableDefinition(anyCollection(), anyInt(), anyString());
    AtomicInteger transferRecipientsCounter = new AtomicInteger(0);
    doAnswer(
            i ->
                new TransferRecipientsResult(
                    String.format(
                        "CORRELATION_ID_%d", transferRecipientsCounter.getAndIncrement())))
        .when(requestExecutor)
        .transferRecipients(anyCollection(), any());
    sut.load(executionContext);
    assertEquals(5, executionContext.getLoadedData().getRecords().size());
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .map(Record::getInfo)
            .allMatch(
                i ->
                    i.isCampaignCreated()
                        && Objects.nonNull(i.getCreatedCampaignId())
                        && i.isDeliveryCreated()
                        && Objects.nonNull(i.getCreatedDeliveryId())
                        && i.isFieldsDefinitionDone()));
    assertTrue(
        executionContext.getLoadedData().getRecords().stream()
            .allMatch(r -> r.getMessages().size() == 4));
    assertEquals(0, executionContext.getLoadedData().getErrors().size());
  }

  @Test
  void aaa() {
    List<String> ips = new ArrayList<>();
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        // filters out 127.0.0.1 and inactive interfaces
        if (iface.isLoopback() || !iface.isUp()) continue;

        Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (Inet6Address.class.equals(addr.getClass())) {
            continue;
          }
          ips.add(addr.getHostAddress());
        }
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
    System.out.println("IPs: " + ips);
  }

  private ExecutionContext createValidExecutionContext(boolean proof) {
    ExecutionContext executionContext = createExecutionContext();
    IntStream.rangeClosed(1, 5).forEach(i -> addRecord(executionContext, i, proof));
    return executionContext;
  }

  private void addRecord(ExecutionContext executionContext, int index, boolean proof) {
    Record record =
        new Record(
            new Info(
                String.format(
                    "CAMPAIGN%d_Campaign %d_DELIVERY%d_Delivery %d_2021-09-01_2021-12-31.csv",
                    index, index, index, index),
                String.format("CAMPAIGN%d", index),
                String.format("Campaign %d", index),
                String.format("DELIVERY%d", index),
                String.format("Delivery %d", index),
                Date.from(
                    LocalDate.of(2021, 9, 1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                Date.from(
                    LocalDate.of(2021, 12, 31)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()),
                proof,
                false,
                false,
                null,
                false,
                false,
                false,
                null,
                false));
    for (Mapping mapping : executionContext.getMappings()) {
      record
          .getValues()
          .add(
              new Value(mapping.getTarget(), String.format("Value %d %d", mapping.getId(), index)));
    }
    executionContext.getTransformedData().getRecords().add(record);
  }

  private String createToken() {
    Token token =
        new Token(
            Date.from(
                    LocalDate.of(2021, 12, 31)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                .getTime());
    Base64.Encoder encoder = Base64.getEncoder();
    return String.format(
        "%s.%s.%s",
        encoder.encodeToString(ALGORITHM_AND_TOKEN_TYPE.getBytes()),
        encoder.encodeToString(BeanConverter.serialize(token).getBytes()),
        RandomStringUtils.randomAlphanumeric(86));
  }
}
