package ru.configuration.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс конфигурации брокера Kafka и его топиков.
 *
 * @author Артем Дружинин.
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Адрес брокера Kafka, для подключения потребителей и производителей Kafka.
     */
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Название топика исходящих сообщений.
     */
    @Value(value = "${spring.kafka.topic-names.outbox}")
    private String outboxTopicName;

    /**
     * Название топика входящих сообщений.
     */
    @Value(value = "${spring.kafka.topic-names.inbox}")
    private String inboxTopicName;

    /**
     * Конфигурация админ-класса, который будет делегировать создание топиков Kafka.
     *
     * @return Возвращает сконфигурированный админ-класс.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * Создание топика исходящих сообщений по имени топика {@code outboxTopicName}
     *
     * @return Возвращает созданный топик Kafka исходящих сообщений.
     */
    @Bean
    public NewTopic topicOutbox() {
        return new NewTopic(outboxTopicName, 1, (short) 1);
    }

    /**
     * Создание топика входящих сообщений по имени топика {@code inboxTopicName}
     *
     * @return Возвращает созданный топик Kafka входящих сообщений.
     */
    @Bean
    public NewTopic topicInbox() {
        return new NewTopic(inboxTopicName, 1, (short) 1);
    }
}