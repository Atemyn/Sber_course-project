package ru.documents.service;

import ru.documents.entity.Inbox;

import java.util.List;

public interface InboxService {
    Inbox save(Inbox inbox);

    List<Inbox> getAllUnreadMessages();

    List<Inbox> setMessagesAsRead(List<Long> messagesIds);
}
