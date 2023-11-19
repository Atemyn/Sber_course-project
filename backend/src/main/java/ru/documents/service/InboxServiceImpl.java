package ru.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.entity.Inbox;
import ru.documents.repository.InboxRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService{

    private final InboxRepository repository;

    @Override
    @Transactional
    public Inbox save(Inbox inbox) {
        if (repository.findById(inbox.getId()).isPresent()) {
            // TODO Написать кастомное исключение.
            throw new RuntimeException();
        }

        return repository.save(inbox);
    }

    @Override
    @Transactional
    public List<Inbox> getAllUnreadMessages() {
        return repository.findAllByIsRead(false);
    }

    @Override
    @Transactional
    public List<Inbox> setMessagesAsRead(List<Long> unreadMessagesIds) {
        List<Inbox> unreadMessages = repository.findAllById(unreadMessagesIds);
        for (var message : unreadMessages) {
            message.setRead(true);
        }
        return repository.saveAll(unreadMessages);
    }
}
