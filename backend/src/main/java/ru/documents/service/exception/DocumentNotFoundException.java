package ru.documents.service.exception;

/**
 * Исключение при ошибке получения/нахождения документа.
 *
 * @author Артем Дружинин.
 */
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
