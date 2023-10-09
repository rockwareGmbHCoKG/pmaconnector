package de.rockware.pma.connector.execution.services.internal;

import static de.rockware.pma.connector.common.utils.TestBeansFactory.createConfigurationValues;
import static de.rockware.pma.connector.common.utils.TestBeansFactory.createMappings;
import static org.mockito.Mockito.*;

import de.rockware.pma.connector.common.enumerations.Step;
import de.rockware.pma.connector.common.factories.Factory;
import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.configuration.services.ConfigurationService;
import de.rockware.pma.connector.execution.beans.ExecutionContext;
import de.rockware.pma.connector.execution.beans.ExecutionError;
import de.rockware.pma.connector.execution.beans.Info;
import de.rockware.pma.connector.execution.beans.Record;
import de.rockware.pma.connector.execution.exceptions.ExecutionServiceException;
import de.rockware.pma.connector.execution.services.ExecutionCompletedService;
import de.rockware.pma.connector.extraction.extractors.Extractor;
import de.rockware.pma.connector.load.loaders.Loader;
import de.rockware.pma.connector.mail.services.MailSenderService;
import de.rockware.pma.connector.mapping.entities.Mapping;
import de.rockware.pma.connector.transformation.transformers.Transformer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceImplTest {

  @Mock private ReadWriteService<Mapping> mappingService;
  @Mock private ConfigurationService configurationService;
  @Mock private MailSenderService mailSenderService;
  @Mock private Factory<Extractor, ExecutionContext> extractorFactory;
  @Mock private Factory<Transformer, ExecutionContext> transformerFactory;
  @Mock private Factory<Loader, ExecutionContext> loaderFactory;
  @Mock private ExecutionCompletedService executionCompletedService;

  @Mock private Extractor extractor;
  @Mock private Transformer transformer;
  @Mock private Loader loader;

  private ExecutionServiceImpl sut;

  private final Info info =
      new Info(
          "TEST_RESOURCE",
          "CAMPAIGN1",
          "Campaign 1",
          "DELIVERY1",
          "Delivery 1",
          Date.from(
              LocalDate.of(2021, 9, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
          Date.from(
              LocalDate.of(2021, 12, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
          true,
          false,
          false,
          null,
          false,
          false,
          false,
          null,
          false);

  @BeforeEach
  void setUp() {
    when(mappingService.getAll()).thenReturn(createMappings());
    when(configurationService.getAll()).thenReturn(createConfigurationValues());
    when(extractorFactory.create(any())).thenReturn(extractor);
    when(transformerFactory.create(any())).thenReturn(transformer);
    when(loaderFactory.create(any())).thenReturn(loader);
    this.sut =
        new ExecutionServiceImpl(
            mappingService,
            configurationService,
            mailSenderService,
            extractorFactory,
            transformerFactory,
            loaderFactory,
            executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void nullMappings() {
    when(mappingService.getAll()).thenReturn(null);
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), isA(ExecutionServiceException.class));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void noMappings() {
    when(mappingService.getAll()).thenReturn(Collections.emptyList());
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), isA(ExecutionServiceException.class));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void nullConfigurationValues() {
    when(configurationService.getAll()).thenReturn(null);
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), isA(ExecutionServiceException.class));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void emptyConfigurationValues() {
    when(configurationService.getAll()).thenReturn(Collections.emptyList());
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), isA(ExecutionServiceException.class));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void extractionWithExceptions() {
    RuntimeException testException = new RuntimeException("Test exception");
    doThrow(testException).when(extractor).extract(any());
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), argThat(e -> e.equals(testException)));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void extractionWithErrors() {
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getErrors()
                  .add(
                      new ExecutionError(
                          info,
                          Step.EXTRACTION,
                          null,
                          "Error executing extraction step",
                          new RuntimeException("Test exception")));
              return null;
            })
        .when(extractor)
        .extract(any());
    sut.run();
    verify(mailSenderService).send(anyString(), anyList(), argThat(e -> e.size() == 1));
    verify(executionCompletedService).process(any());
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void skippedTransformationWithExceptions() {
    RuntimeException testException = new RuntimeException("Test exception");
    doThrow(testException).when(transformer).transform(any());
    sut.run();
    verify(executionCompletedService).process(any());
    verifyNoInteractions(mailSenderService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void transformationWithExceptions() {
    Info info = new Info();
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(extractor)
        .extract(any());
    RuntimeException testException = new RuntimeException("Test exception");
    doThrow(testException).when(transformer).transform(any());
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), argThat(e -> e.equals(testException)));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void skippedTransformationWithErrors() {
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getTransformedData()
                  .getErrors()
                  .add(
                      new ExecutionError(
                          info,
                          Step.TRANSFORMATION,
                          null,
                          "Error executing transformation step",
                          new RuntimeException("Test exception")));
              return null;
            })
        .when(transformer)
        .transform(any());
    sut.run();
    verify(executionCompletedService).process(any());
    verifyNoInteractions(mailSenderService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void transformationWithErrors() {
    Info info = new Info();
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(extractor)
        .extract(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getTransformedData()
                  .getErrors()
                  .add(
                      new ExecutionError(
                          info,
                          Step.TRANSFORMATION,
                          null,
                          "Error executing transformation step",
                          new RuntimeException("Test exception")));
              return null;
            })
        .when(transformer)
        .transform(any());
    sut.run();
    verify(mailSenderService).send(anyString(), anyList(), argThat(e -> e.size() == 1));
    verify(executionCompletedService).process(any());
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void skippedLoadingWithExceptions() {
    RuntimeException testException = new RuntimeException("Test exception");
    doThrow(testException).when(loader).load(any());
    sut.run();
    verify(executionCompletedService).process(any());
    verifyNoInteractions(mailSenderService);
  }

  @Test
  void loadingWithExceptions() {
    Info info = new Info();
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(extractor)
        .extract(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getTransformedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(transformer)
        .transform(any());
    RuntimeException testException = new RuntimeException("Test exception");
    doThrow(testException).when(loader).load(any());
    sut.run();
    verify(mailSenderService)
        .send(anyString(), anyList(), anyString(), argThat(e -> e.equals(testException)));
    verifyNoInteractions(executionCompletedService);
  }

  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void skippedLoadingWithErrors() {
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getLoadedData()
                  .getErrors()
                  .add(
                      new ExecutionError(
                          info,
                          Step.LOAD,
                          null,
                          "Error executing loading step",
                          new RuntimeException("Test exception")));
              return null;
            })
        .when(loader)
        .load(any());
    sut.run();
    verify(executionCompletedService).process(any());
    verifyNoInteractions(mailSenderService);
  }

  @Test
  void loadingWithErrors() {
    Info info = new Info();
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(extractor)
        .extract(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getTransformedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(transformer)
        .transform(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getLoadedData()
                  .getErrors()
                  .add(
                      new ExecutionError(
                          info,
                          Step.LOAD,
                          null,
                          "Error executing loading step",
                          new RuntimeException("Test exception")));
              return null;
            })
        .when(loader)
        .load(any());
    sut.run();
    verify(mailSenderService).send(anyString(), anyList(), argThat(e -> e.size() == 1));
    verify(executionCompletedService).process(any());
  }

  @Test
  void success() {
    Info info = new Info();
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getExtractedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(extractor)
        .extract(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getTransformedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(transformer)
        .transform(any());
    doAnswer(
            i -> {
              i.getArgument(0, ExecutionContext.class)
                  .getLoadedData()
                  .getRecords()
                  .add(new Record(info));
              return null;
            })
        .when(loader)
        .load(any());
    sut.run();
    verifyNoInteractions(mailSenderService);
    verify(executionCompletedService).process(any());
  }
}
