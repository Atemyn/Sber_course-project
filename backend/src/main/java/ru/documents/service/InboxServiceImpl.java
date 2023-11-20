package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.entity.Inbox;
import ru.documents.repository.InboxRepository;
import ru.documents.service.exception.InboxDuplicateSaveAttemptException;
import ru.documents.service.exception.MessageNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Сервис по работе с входящими сообщениями.
 *
 * @author Артем Дружинин.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InboxServiceImpl implements InboxService {

    /**
     * Репозиторий по работе с входящими сообщениями.
     */
    private final InboxRepository repository;

    /**
     * Метод для сохранения входящего сообщения.
     *
     * @param inbox Входящее сообщение, которое нужно сохранить.
     * @return Возвращает сохраненное входящее сообщение.
     * @throws InboxDuplicateSaveAttemptException при попытке сохранения дубликата входящего сообщения.
     */
    @Override
    @Transactional
    public Inbox save(Inbox inbox) {
        if (repository.findById(inbox.getId()).isPresent()) {
            throw new InboxDuplicateSaveAttemptException(
                    String.format("Inbox duplicate with id %d save attempt detected", inbox.getId()));
        }

        Inbox savedInbox = repository.save(inbox);
        log.info("Payload was successfully saved to Inbox table. Payload: {}", savedInbox);
        return savedInbox;
    }

    /**
     * Метод для получения всех непрочитанных входящих сообщений.
     *
     * @return Возвращает список всех непрочитанных входящих сообщений.
     */
    @Override
    @Transactional
    public List<Inbox> getAllUnreadMessages() {
        List<Inbox> allByIsRead = repository.findAllByIsRead(false);
        log.info("Getting unread objects from Inbox table result: {}", allByIsRead);
        return allByIsRead;
    }

    /**
     * Метод для пометки всех входящих сообщений,
     * соответствующих номерам из переданного списка, как прочтенных.
     *
     * @param unreadMessagesIds Список номеров сообщений, которые нужно пометить прочитанными.
     * @return Возвращает список помеченных прочитанными входящих сообщений.
     */
    @Override
    @Transactional
    public List<Inbox> setMessagesAsRead(List<Long> unreadMessagesIds) {
        List<Inbox> unreadMessages = repository.findAllById(unreadMessagesIds);
        for (var message : unreadMessages) {
            message.setRead(true);
        }
        List<Inbox> inboxes = repository.saveAll(unreadMessages);
        log.info("Objects from Inbox table were successfully read. Objects: {}", inboxes);
        return inboxes;
    }

    /**
     * Метод для пометки сообщения прочитанным по его номеру.
     * @param messageId Номер сообщения.
     *
     * @return Возвращает прочитанное сообщение.
     * @throws MessageNotFoundException если сообщение с таким номером не найдено.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Inbox setMessageAsRead(Long messageId) {
        Optional<Inbox> messageOptional = repository.findById(messageId);
        if (messageOptional.isEmpty()) {
            throw new MessageNotFoundException(
                    String.format("Inbox with id %d not found", messageId));
        }

        Inbox message = messageOptional.get();
        message.setRead(true);
        log.info("SAVED READ 1 id: {}", messageId);

        return repository.save(message);
    }
}
