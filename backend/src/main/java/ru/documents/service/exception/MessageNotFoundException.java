package ru.documents.service.exception;

/**
 * Исключение при ошибке получения/нахождения сообщения.
 *
 * @author Артем Дружинин.
 */
public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String message) {
        super(message);
    }
}
