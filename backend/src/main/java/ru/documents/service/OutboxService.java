package ru.documents.service;

import ru.documents.entity.Outbox;

public interface OutboxService {
    Outbox saveMessage(Object payload);

    void sendAllUnsentMessagesToKafka();
}
