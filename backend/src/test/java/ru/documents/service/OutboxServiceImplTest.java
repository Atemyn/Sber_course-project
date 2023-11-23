package ru.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.configuration.JacksonConfiguration;
import ru.documents.entity.Document;
import ru.documents.entity.Outbox;
import ru.documents.repository.OutboxRepository;
import ru.documents.service.exception.PayloadToJsonProcessingException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class OutboxServiceImplTest {
    private OutboxServiceImpl outboxService;

    private ObjectMapper mapper;

    private OutboxRepository repository;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    @DisplayName("Первоначальная инициализация всех компонентов")
    public void initializeComponents() {
        mapper = spy(new JacksonConfiguration().testObjectMapper());
        repository = mock(OutboxRepository.class);
        kafkaProducer = mock(KafkaProducer.class);
        outboxService = new OutboxServiceImpl(mapper, repository, kafkaProducer);
    }

    @Test
    @DisplayName("Тест на успешное сохранение исходящего сообщения")
    public void testMessageSaveIsSuccessful() throws JsonProcessingException {
        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(mapper.writeValueAsString(document)).thenCallRealMethod();

        Outbox expectedOutbox = new Outbox(1L, mapper.writeValueAsString(document));

        when(repository.save(any(Outbox.class))).then(method -> {
           Outbox outbox = (Outbox) method.getArguments()[0];
           outbox.setId(1L);
           return outbox;
        });

        assertEquals(expectedOutbox, outboxService.saveMessage(document));
    }

    @Test
    @DisplayName("Тест на получение PayloadToJsonProcessingException при ошибке преобразования документа в JSON")
    public void testPayloadToJsonProcessingExceptionWhenErrorProcessingDocumentToJson()
            throws JsonProcessingException {
        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(mapper.writeValueAsString(document)).thenThrow(JsonProcessingException.class);

        assertThrows(PayloadToJsonProcessingException.class, () -> outboxService.saveMessage(document));
    }

    @Test
    @DisplayName("Тест на успешную пометку сообщений как отправленных при отправке сообщений")
    public void testMessagesAreMarkedAsSentSuccessfully() throws JsonProcessingException {
        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(mapper.writeValueAsString(document)).thenCallRealMethod();

        Outbox outbox = new Outbox(1L, mapper.writeValueAsString(document));

        Outbox sentOutbox = new Outbox(1L, mapper.writeValueAsString(document));
        sentOutbox.setSent(true);

        when(repository.findAllByIsSent(false)).thenReturn(List.of(outbox));
        doNothing().when(kafkaProducer).sendAsync(outbox);
        when(repository.saveAll(any(List.class))).then(method -> method.getArguments()[0]);

        outboxService.sendAllUnsentMessagesToKafka();

        assertEquals(sentOutbox, outbox);
    }
}
