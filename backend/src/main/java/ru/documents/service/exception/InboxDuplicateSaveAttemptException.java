package ru.documents.service.exception;

/**
 * Исключение при попытке сохранения в сущность Inbox записи-дубликата.
 *
 * @author Артем Дружинин.
 */
public class InboxDuplicateSaveAttemptException extends RuntimeException {
    public InboxDuplicateSaveAttemptException(String message) {
        super(message);
    }
}
