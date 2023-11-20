package ru.documents.service.exception;

/**
 * Исключение о неверном статусе документа.
 */
public class WrongDocumentStatusException extends RuntimeException {
    public WrongDocumentStatusException(String message) {
        super(message);
    }
}
