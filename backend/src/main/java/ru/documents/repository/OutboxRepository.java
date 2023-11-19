package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Outbox;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findAllByIsSent(boolean isSent);
}
