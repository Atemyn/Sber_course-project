package ru.documents.service;

import ma.glasnost.orika.impl.ConfigurableMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;

import ru.documents.controller.dto.InboxDocumentProcessingResult;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;
import ru.documents.entity.Inbox;
import ru.documents.mapping.DocumentMapperOrika;
import ru.documents.repository.DocumentRepository;
import ru.documents.service.exception.DocumentNotFoundException;
import ru.documents.service.exception.WrongDocumentStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentServiceImplTest {
    private DocumentServiceImpl documentService;

    private ConfigurableMapper mapper;

    private DocumentRepository repository;

    private InboxService inboxService;

    @BeforeEach
    @DisplayName("Первоначальная инициализация всех компонентов")
    public void initializeComponents() {
        mapper = new DocumentMapperOrika();
        repository = mock(DocumentRepository.class);
        inboxService = mock(InboxService.class);
        documentService = new DocumentServiceImpl(mapper,
                repository, mock(OutboxService.class), inboxService);
    }

    @Test
    @DisplayName("Тест на успешное сохранение документа")
    public void testSuccessfulDocumentSave() {
        DocumentDto documentDto = new DocumentDto(
                null,
                "type",
                "organization",
                "description",
                null,
                "patient",
                null);

        DocumentDto expectedSavedDocumentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                Status.of(StatusEnum.NEW.name(), StatusEnum.NEW.getExtendedName()));

        when(repository.save(any(Document.class)))
                .then(method -> {
                    Document document = (Document) method.getArguments()[0];
                    document.setId(1L);
                    return document;
                });

        DocumentDto actualSavedDocumentDto = documentService.save(documentDto);

        assertEquals(expectedSavedDocumentDto.getId(), actualSavedDocumentDto.getId());
        assertEquals(expectedSavedDocumentDto.getType(), actualSavedDocumentDto.getType());
        assertEquals(expectedSavedDocumentDto.getOrganization(), actualSavedDocumentDto.getOrganization());
        assertEquals(expectedSavedDocumentDto.getDescription(), actualSavedDocumentDto.getDescription());
        assertEquals(expectedSavedDocumentDto.getPatient(), actualSavedDocumentDto.getPatient());
        assertEquals(expectedSavedDocumentDto.getStatus(), actualSavedDocumentDto.getStatus());
        assertTrue((new Date().getTime() - actualSavedDocumentDto.getDate().getTime()) < 3000);
    }

    @Test
    @DisplayName("Тест на успешную отправку документа на обработку")
    public void testSuccessfulDocumentSendToProcessing() {
        IdDto idDto = new IdDto(1L);

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "NEW");

        DocumentDto expectedSavedDocumentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                document.getDate(),
                "patient",
                Status.of(StatusEnum.IN_PROCESS.name(), StatusEnum.IN_PROCESS.getExtendedName()));

        when(repository.findById(idDto.getId())).thenReturn(Optional.of(document));
        when(repository.save(any(Document.class))).then(method -> method.getArguments()[0]);

        DocumentDto actualSavedDocumentDto = documentService.sendToProcessing(idDto);
        assertEquals(expectedSavedDocumentDto, actualSavedDocumentDto);
    }

    @Test
    @DisplayName("Тест на получение DocumentNotFoundException при" +
            " попытке отправки несуществующего документа на обработку")
    public void testDocumentNotFoundExceptionWhenSendingNonExistingDocumentToProcessing() {
        IdDto idDto = new IdDto(1L);

        when(repository.findById(idDto.getId())).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.sendToProcessing(idDto));
    }

    @Test
    @DisplayName("Тест на получение WrongDocumentStatusException при" +
            " попытке отправки документа с некорректным статусом на обработку")
    public void testWrongDocumentStatusExceptionWhenSendingNonExistingDocumentToProcessing() {
        IdDto idDto = new IdDto(1L);

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS"); // Должен быть NEW

        when(repository.findById(idDto.getId())).thenReturn(Optional.of(document));

        assertThrows(WrongDocumentStatusException.class, () -> documentService.sendToProcessing(idDto));
    }

    @Test
    @DisplayName("Тест на успешное получение всех документов")
    public void testSuccessfulFindAllDocuments() {
        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(repository.findAll()).thenReturn(List.of(document));

        assertEquals(Stream.of(document).map(
                d -> mapper.map(d, DocumentDto.class)).
                collect(Collectors.toList()), documentService.findAll());
    }

    @Test
    @DisplayName("Тест на успешное получение документа по id")
    public void testSuccessfulFindDocumentById() {
        Long id = 1L;
        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(repository.findById(id)).thenReturn(Optional.of(document));

        assertEquals(mapper.map(document, DocumentDto.class), documentService.findById(id));
    }

    @Test
    @DisplayName("Тест на получение DocumentNotFoundException" +
            " при попытке найти по id несуществующий документ")
    public void testDocumentNotFoundExceptionWhenLookingForNonExistingDocument() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.findById(id));
    }

    @Test
    @DisplayName("Тест на получение WrongDocumentStatusException" +
            " при попытке обновления документа с некорректным статусом")
    public void testWrongDocumentStatusExceptionWhenUpdatingDocumentWithWrongStatusFromInbox() {
        Long documentId = 1L;

        Inbox inbox = new Inbox(1L,
                new InboxDocumentProcessingResult(documentId, StatusEnum.ACCEPTED.name()));

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "NEW"); // Должен быть IN_PROCESS

        when(inboxService.getAllUnreadMessages()).thenReturn(List.of(inbox));
        when(repository.findAllById(List.of(documentId))).thenReturn(List.of(document));

        assertThrows(WrongDocumentStatusException.class, () -> documentService.readInboxAndUpdateDocumentsStatus());
    }

    @Test
    @DisplayName("Тест на получение WrongDocumentStatusException" +
            " при попытке присвоения документу неверного статуса")
    public void testWrongDocumentStatusExceptionWhenUpdatingDocumentFromInboxWithWrongStatus() {
        Long documentId = 1L;

        Inbox inbox = new Inbox(1L,
                new InboxDocumentProcessingResult(documentId, "WRONG_STATUS"));

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                "IN_PROCESS");

        when(inboxService.getAllUnreadMessages()).thenReturn(List.of(inbox));
        when(repository.findAllById(List.of(documentId))).thenReturn(List.of(document));

        assertThrows(WrongDocumentStatusException.class, () -> documentService.readInboxAndUpdateDocumentsStatus());
    }
}
