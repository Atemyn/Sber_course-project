package ru.documents.service.exception;

/**
 * Исключение при валидации некорректных полей документа.
 *
 * @author Артем Дружинин.
 */
public class DocumentFieldsNotValidException extends RuntimeException {
    public DocumentFieldsNotValidException(String message) {
        super(message);
    }
}
