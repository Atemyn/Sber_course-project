package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Inbox;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
    List<Inbox> findAllByIsRead(boolean isRead);
}
