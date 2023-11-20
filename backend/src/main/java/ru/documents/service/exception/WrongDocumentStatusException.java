package ru.documents.service.exception;

/**
 * Исключение о неверном статусе документа.
 *
 * @author Артем Дружинин.
 */
public class WrongDocumentStatusException extends RuntimeException {
    public WrongDocumentStatusException(String message) {
        super(message);
    }

    public WrongDocumentStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
