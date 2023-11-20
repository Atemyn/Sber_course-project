package ru.documents.service;

import ru.documents.entity.Outbox;

/**
 * Интерфейс сервиса по работе с исходящими сообщениями.
 *
 * @author Артем Дружинин.
 */
public interface OutboxService {
    /**
     * Метод для сохранения исходящего сообщения в базе данных.
     *
     * @param payload Основная часть сообщения - информация о документе, отправленном на обработку.
     * @return Возвращает сохраненное в базе данных исходящее сообщение.
     */
    Outbox saveMessage(Object payload);

    /**
     * Метод по отправке всех неотправленных исходящих сообщений в Kafka.
     */
    void sendAllUnsentMessagesToKafka();
}
