package ru.documents.service.exception;

/**
 * Исключение при ошибке получения/нахождения документа.
 */
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
