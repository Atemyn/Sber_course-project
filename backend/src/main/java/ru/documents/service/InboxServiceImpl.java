package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.entity.Inbox;
import ru.documents.repository.InboxRepository;
import ru.documents.service.exception.InboxDuplicateSaveAttemptException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxServiceImpl implements InboxService{

    private final InboxRepository repository;

    @Override
    @Transactional
    public Inbox save(Inbox inbox) {
        if (repository.findById(inbox.getId()).isPresent()) {
            throw new InboxDuplicateSaveAttemptException(
                    String.format("Inbox duplicate with id %d save attempt detected", inbox.getId()));
        }

        Inbox savedInbox = repository.save(inbox);
        log.info("Payload was successfully saved to Inbox table. Payload: " + savedInbox);
        return savedInbox;
    }

    @Override
    @Transactional
    public List<Inbox> getAllUnreadMessages() {
        List<Inbox> allByIsRead = repository.findAllByIsRead(false);
        log.info("Getting unread objects from Inbox table result: " + allByIsRead);
        return allByIsRead;
    }

    @Override
    @Transactional
    public List<Inbox> setMessagesAsRead(List<Long> unreadMessagesIds) {
        List<Inbox> unreadMessages = repository.findAllById(unreadMessagesIds);
        for (var message : unreadMessages) {
            message.setRead(true);
        }
        List<Inbox> inboxes = repository.saveAll(unreadMessages);
        log.info("Objects from Inbox table were successfully read. Objects: " + inboxes);
        return inboxes;
    }
}
