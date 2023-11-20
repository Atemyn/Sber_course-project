package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.documents.controller.dto.InboxDocumentProcessingResult;
import ru.documents.entity.Inbox;

import java.util.Optional;

/**
 * Сервис по чтению входящих сообщений из Kafka.
 *
 * @author Артем Дружинин.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    /**
     * Сервис по работе с входящими сообщениями.
     */
    private final InboxService inboxService;

    /**
     * Метод для чтения входящего сообщения из топика Kafka, указанного в качестве
     * параметра {@code topics} в аннотации {@link KafkaListener}
     * и сохранения этого сообщения через {@link InboxService}
     *
     * @param messageId                Номер входящего сообщения.
     * @param documentProcessingResult Основная часть сообщения - результат обработки документа.
     * @return Возвращает сохраненное сообщение.
     */
    @KafkaListener(topics = "${spring.kafka.topic-names.inbox}")
    public Optional<Inbox> consumeInbox(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long messageId,
                                        @Payload InboxDocumentProcessingResult documentProcessingResult) {
        Inbox inbox = inboxService.save(new Inbox(messageId, documentProcessingResult));
        return Optional.of(inbox);
    }
}
