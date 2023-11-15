package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
