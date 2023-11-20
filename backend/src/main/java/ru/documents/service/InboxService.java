package ru.documents.service;

import ru.documents.entity.Inbox;

import java.util.List;

/**
 * Интерфейс сервиса по работе с входящими сообщениями.
 *
 * @author Артем Дружинин.
 */
public interface InboxService {
    /**
     * Метод для сохранения входящего сообщения.
     *
     * @param inbox Входящее сообщение, которое нужно сохранить.
     * @return Возвращает сохраненное входящее сообщение.
     */
    Inbox save(Inbox inbox);

    /**
     * Метод для получения всех непрочитанных входящих сообщений.
     *
     * @return Возвращает список всех непрочитанных входящих сообщений.
     */
    List<Inbox> getAllUnreadMessages();

    /**
     * Метод для пометки всех входящих сообщений,
     * соответствующих номерам из переданного списка, как прочтенных.
     *
     * @param unreadMessagesIds Список номеров сообщений, которые нужно пометить прочитанными.
     * @return Возвращает список помеченных прочитанными входящих сообщений.
     */
    List<Inbox> setMessagesAsRead(List<Long> unreadMessagesIds);

    /**
     * Метод для пометки сообщения прочитанным по его номеру.
     * @param messageId Номер сообщения.
     *
     * @return Возвращает прочитанное сообщение.
     */
    Inbox setMessageAsRead(Long messageId);
}
