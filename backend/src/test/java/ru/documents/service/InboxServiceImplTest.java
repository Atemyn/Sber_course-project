package ru.documents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.documents.controller.dto.InboxDocumentProcessingResult;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Inbox;
import ru.documents.repository.InboxRepository;
import ru.documents.service.exception.InboxDuplicateSaveAttemptException;
import ru.documents.service.exception.MessageNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InboxServiceImplTest {
    private InboxServiceImpl service;

    private InboxRepository repository;

    @BeforeEach
    @DisplayName("Первоначальная инициализация всех компонентов")
    public void initializeComponents() {
        repository = mock(InboxRepository.class);
        service = new InboxServiceImpl(repository);
    }

    @Test
    @DisplayName("Тест на успешное сохранение сообщения")
    public void testSuccessfulMessageSave() {
        Inbox inbox = new Inbox(1L,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));

        when(repository.findById(inbox.getId())).thenReturn(Optional.empty());
        when(repository.save(any(Inbox.class))).then(method -> method.getArguments()[0]);

        assertEquals(inbox, service.save(inbox));
    }

    @Test
    @DisplayName("Тест на получение InboxDuplicateSaveAttemptException" +
            " при попытке сохранения дубликата сообщения")
    public void testInboxDuplicateSaveAttemptExceptionWhenSavingDuplicateMessage() {
        Inbox inbox = new Inbox(1L,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));

        when(repository.findById(inbox.getId())).thenReturn(Optional.of(inbox));

        assertThrows(InboxDuplicateSaveAttemptException.class, () -> service.save(inbox));
    }

    @Test
    @DisplayName("Тест на успешное получение всех непрочитанных сообщений")
    public void testSuccessfulGettingAllUnreadMessages() {
        Inbox inbox = new Inbox(1L,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));

        when(repository.findAllByIsRead(false)).thenReturn(List.of(inbox));

        assertEquals(List.of(inbox), service.getAllUnreadMessages());
    }

    @Test
    @DisplayName("Тест на успешную пометку всех сообщений как прочитанных")
    public void testSuccessfulSettingAllMessagesAsRead() {
        long messageId = 1L;
        Inbox inbox = new Inbox(messageId,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));
        List<Inbox> unreadInboxes = List.of(inbox);

        Inbox readInbox = new Inbox(messageId,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));
        readInbox.setRead(true);

        when(repository.findAllById(List.of(messageId))).thenReturn(unreadInboxes);
        when(repository.saveAll(any(List.class))).then(method -> method.getArguments()[0]);

        assertEquals(List.of(readInbox), service.setMessagesAsRead(List.of(messageId)));
    }

    @Test
    @DisplayName("Тест на успешную пометку одного сообщения как прочитанного")
    public void testSuccessfulSettingMessageAsRead() {
        long messageId = 1L;
        Inbox inbox = new Inbox(messageId,
                new InboxDocumentProcessingResult(1L, StatusEnum.ACCEPTED.name()));

        when(repository.findById(messageId)).thenReturn(Optional.of(inbox));
        when(repository.save(any(Inbox.class))).then(method -> method.getArguments()[0]);

        assertTrue(service.setMessageAsRead(messageId).isRead());
    }

    @Test
    @DisplayName("Тест на получение MessageNotFoundException" +
            " при попытке пометить прочитанным несуществующее сообщение")
    public void testMessageNotFoundExceptionWhenMarkingNonExistingMessageAsRead() {
        long messageId = 1L;

        when(repository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> service.setMessageAsRead(messageId));
    }
}
