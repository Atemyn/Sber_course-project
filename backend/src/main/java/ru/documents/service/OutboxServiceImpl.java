package ru.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.entity.Outbox;
import ru.documents.repository.OutboxRepository;
import ru.documents.service.exception.KafkaSendingException;
import ru.documents.service.exception.PayloadToJsonProcessingException;

import java.util.List;

/**
 * Сервис по работе с исходящими сообщениями.
 *
 * @author Артем Дружинин.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    /**
     * Маппер объекта в JSON формат.
     */
    private final ObjectMapper mapper;

    /**
     * Репозиторий по работе с исходящими сообщениями.
     */
    private final OutboxRepository repository;

    /**
     * Сервис по отправке исходящих сообщений в Kafka.
     */
    private final KafkaProducer kafkaProducer;

    /**
     * Метод для сохранения исходящего сообщения в базе данных.
     *
     * @param payload Основная часть сообщения - информация о документе, отправленном на обработку.
     * @return Возвращает сохраненное в базе данных исходящее сообщение.
     * @throws PayloadToJsonProcessingException при ошибке преобразования объекта в JSON формат.
     */
    @Override
    @Transactional
    public Outbox saveMessage(Object payload) {
        String jsonPayload;
        try {
            jsonPayload = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new PayloadToJsonProcessingException(
                    String.format("Error when processing object %s to json format", payload), e);
        }

        Outbox savedOutbox = repository.save(new Outbox(null, jsonPayload));
        log.info("Payload was successfully saved to Outbox table. Payload: {}", savedOutbox);
        return savedOutbox;
    }

    /**
     * Метод для запланированной периодической (с фиксированной задержкой) отправки
     * всех неотправленных исходящих сообщений в Kafka.
     *
     * @throws KafkaSendingException при ошибке отправки сообщения.
     */
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
