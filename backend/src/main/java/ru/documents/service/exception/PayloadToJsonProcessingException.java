package ru.documents.service.exception;

/**
 * Исключение при неудачной попытке преобразования объекта в Json.
 */
public class PayloadToJsonProcessingException extends RuntimeException {
    public PayloadToJsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
