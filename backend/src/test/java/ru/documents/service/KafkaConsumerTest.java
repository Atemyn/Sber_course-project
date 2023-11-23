package ru.documents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.documents.controller.dto.InboxDocumentProcessingResult;
import ru.documents.entity.Inbox;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KafkaConsumerTest {

    private KafkaConsumer kafkaConsumer;

    private InboxService inboxService;

    private Validator validator;

    @BeforeEach
    @DisplayName("Первоначальная инициализация всех компонентов")
    public void initializeComponents() {
        inboxService = mock(InboxService.class);
        validator = mock(Validator.class);
        kafkaConsumer = new KafkaConsumer(inboxService, validator);
    }

    @Test
    @DisplayName("Тест на успешное получение сообщения и сохранение его через InboxService")
    public void testMessageConsumeIsSuccessful() {
        long messageId = 1L;
        var inboxDocumentProcessingResult =
                new InboxDocumentProcessingResult(1L, "ACCEPTED");
        Inbox inbox = new Inbox(messageId, inboxDocumentProcessingResult);

        when(validator.validate(inboxDocumentProcessingResult)).thenReturn(Set.of());
        when(inboxService.save(any(Inbox.class))).then(method -> method.getArguments()[0]);

        assertEquals(Optional.of(inbox),
                kafkaConsumer.consumeInbox(messageId, inboxDocumentProcessingResult));
    }

    @Test
    @DisplayName("Тест на получение Optional.empty() при получении некорректных параметров в методе")
    public void testGetOptionalEmptyWhenArgumentsAreNotValid() {
        var inboxDocumentProcessingResult =
                new InboxDocumentProcessingResult(1L, "ACCEPTED");

        ConstraintViolation<InboxDocumentProcessingResult> constraintViolation =
                mock(ConstraintViolation.class);
        when(validator.validate(inboxDocumentProcessingResult)).thenReturn(Set.of(constraintViolation));

        assertEquals(Optional.empty(),
                kafkaConsumer.consumeInbox(null, inboxDocumentProcessingResult));
    }

}
