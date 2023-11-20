package ru.documents.service.exception;

/**
 * Исключение при неудачной попытке преобразования объекта в Json.
 *
 * @author Артем Дружинин.
 */
public class PayloadToJsonProcessingException extends RuntimeException {
    public PayloadToJsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
