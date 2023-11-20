package ru.configuration.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.documents.entity.Outbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс конфигурации для производителей Kafka.
 *
 * @author Артем Дружинин.
 */
@Configuration
public class KafkaProducerConfig {
    /**
     * Адрес брокера Kafka, который производители используют для связи с кластером Kafka.
     */
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    /**
     * Конфигурация фабрики производителей Kafka.
     *
     * @return Возвращает сконфигурированную фабрику производителей Kafka.
     * Первое значение в дженерике <> означает тип ключа отправляемого сообщения,
     * а второе - тип основной части сообщения.
     */
    @Bean
    public ProducerFactory<Long, Outbox> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Создание шаблона производителя Kafka для
     * использования высокоуровневых методов по отправке сообщений в Kafka.
     *
     * @return Возвращает шаблон, созданный на основе фабрики {@link ProducerFactory}, настроенной ранее.
     * Первое значение в дженерике <> означает тип ключа отправляемого сообщения,
     * а второе - тип основной части сообщения.
     */
    @Bean
    public KafkaTemplate<Long, Outbox> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}