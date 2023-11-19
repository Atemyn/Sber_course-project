package ru.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.documents.controller.dto.InboxDocumentProcessingResult;
import ru.documents.entity.Inbox;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final InboxService inboxService;

    @KafkaListener(topics = "${spring.kafka.topic-names.inbox}")
    public Optional<Inbox> consumeInbox(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long messageId,
                                        @Payload InboxDocumentProcessingResult documentProcessingResult) {
        try {
            Inbox inbox = inboxService.save(new Inbox(messageId, documentProcessingResult));
            return Optional.of(inbox);
            // TODO Добавить обработку кастомного исключения
        } catch (RuntimeException e) {

        }
        return Optional.empty();
    }
}
