package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Document;

/**
 * Интерфейс JPA репозитория для сущности {@link Document} для
 * осуществления действий с этой сущностью в базе данных.
 *
 * @author Артем Дружинин.
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
