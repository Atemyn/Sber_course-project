package ru.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.documents.controller.dto.InboxDocumentProcessingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс конфигурации для потребителя Kafka.
 *
 * @author Артем Дружинин.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    /**
     * Адрес брокера Kafka, который потребители используют для связи с кластером Kafka.
     */
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Название группы потребителей.
     */
    private final static String CONSUMER_GROUP_ID = "consumer-group-id-1";

    /**
     * Конфигурация фабрики потребителей Kafka.
     *
     * @return Возвращает сконфигурированную фабрику потребителей Kafka.
     * Первое значение в дженерике <> означает тип ключа получаемого сообщения,
     * а второе - тип основной части сообщения.
     */
    public ConsumerFactory<Long,
            InboxDocumentProcessingResult> consumerFactory() {

        Map<String, Object> config = new HashMap<>();
        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                CONSUMER_GROUP_ID);
        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class);
        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new LongDeserializer(),
                new JsonDeserializer<>(InboxDocumentProcessingResult.class));
    }

    /**
     * Конфигурация фабрики для KafkaListener.
     *
     * @return Возвращает сконфигурированную фабрику KafkaListener'ов.
     * Первое значение в дженерике <> означает тип ключа получаемого сообщения,
     * а второе - тип основной части сообщения.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long,
            InboxDocumentProcessingResult> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long,
                InboxDocumentProcessingResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setSyncCommits(true);
        return factory;
    }
}