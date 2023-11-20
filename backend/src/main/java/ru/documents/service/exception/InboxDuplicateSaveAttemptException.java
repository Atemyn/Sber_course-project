package ru.documents.service.exception;

/**
 * Исключение при попытке сохранения в сущность Inbox записи-дубликата.
 */
public class InboxDuplicateSaveAttemptException extends RuntimeException {
    public InboxDuplicateSaveAttemptException(String message) {
        super(message);
    }
}
