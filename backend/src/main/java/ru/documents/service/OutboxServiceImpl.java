package ru.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.entity.Outbox;
import ru.documents.repository.OutboxRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService{

    private final ObjectMapper mapper;

    private final OutboxRepository repository;

    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public Outbox saveMessage(Object payload) {
        String jsonPayload;
        try {
            jsonPayload = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            // TODO Добавить кастомное исключение.
            throw new RuntimeException(e);
        }

        return repository.save(new Outbox(null, jsonPayload));
    }

    @Override
    @Scheduled(fixedDelay = 4000)
    @Transactional
    public void sendAllUnsentMessagesToKafka() {
        List<Outbox> allUnsentMessages = repository.findAllByIsSent(false);
        for (var message : allUnsentMessages) {
            kafkaProducer.sendAsync(message);
            message.setSent(true);
        }
        // Обновление в базе данных у всех выбранных сообщений статуса на "отправлено".
        repository.saveAll(allUnsentMessages);
    }
}
