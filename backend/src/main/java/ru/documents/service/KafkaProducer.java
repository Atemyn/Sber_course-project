package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.documents.entity.Outbox;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    @Value(value = "${spring.kafka.topic-names.outbox}")
    private String topicName;

    private final KafkaTemplate<Long, Outbox> kafkaTemplate;

    public void sendAsync(Outbox message) {
        ListenableFuture<SendResult<Long, Outbox>> future =
                kafkaTemplate.send(topicName, message.getId(), message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.warn(String.format("Error sending message %s to the topic %s. " +
                        "Exception message: %s", message, topicName, ex.getMessage()));
            }

            @Override
            public void onSuccess(SendResult<Long, Outbox> result) {
                log.info(String.format("Message %s was successfully sent to the topic %s.",
                        message, topicName));
            }
        });
    }
}
