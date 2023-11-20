package ru.documents.service.exception;

/**
 * Исключение при ошибке отправки сообщения в Kafka.
 *
 * @author Артем Дружинин.
 */
public class KafkaSendingException extends RuntimeException {
    public KafkaSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
