package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;
import ru.documents.entity.Inbox;
import ru.documents.repository.DocumentRepository;
import ru.documents.service.exception.DocumentNotFoundException;
import ru.documents.service.exception.PayloadToJsonProcessingException;
import ru.documents.service.exception.WrongDocumentStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис по работе с документами.
 *
 * @author Артем Дружинин.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    /**
     * Маппер для преобразования {@link Document} в {@link DocumentDto} и обратно.
     */
    private final ConfigurableMapper mapper;

    /**
     * Репозиторий по работе с документами.
     */
    private final DocumentRepository repository;

    /**
     * Сервис исходящих сообщений.
     */
    private final OutboxService outboxService;

    /**
     * Сервис входящих сообщений.
     */
    private final InboxService inboxService;

    /**
     * Метод для сохранения документа.
     *
     * @param documentDto Документ, который нужно сохранить.
     * @return Возвращает сохраненный документ.
     */
    @Override
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        documentDto.setId(null);
        documentDto.setDate(new Date());
        documentDto.setStatus(Status.of(StatusEnum.NEW.name(),
                StatusEnum.NEW.getExtendedName()));
        documentDto = mapper.map(repository.save(
                mapper.map(documentDto, Document.class)), DocumentDto.class);
        log.info("Document was successfully saved. Document: " + documentDto);
        return documentDto;
    }

    /**
     * Метод для удаления всех документов по списку их номеров.
     *
     * @param ids Номера документов, которые нужно удалить.
     * @throws DocumentNotFoundException если документ не был найден.
     */
    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        for (Long id : ids) {
            deleteById(id);
        }
    }

    /**
     * Метод для удаления документа по его номеру.
     *
     * @param id Номер документа, который нужно удалить.
     * @throws DocumentNotFoundException если документ не был найден.
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        var document = getById(id);
        repository.delete(document);
        log.info(String.format("Document with id %d was successfully deleted", id));
    }

    /**
     * Метод для отправки документа на обработку по его номеру.
     *
     * @param idDto Номер документа.
     * @return Возвращает отправленный на обработку документ.
     * @throws DocumentNotFoundException        если документ не был найден.
     * @throws WrongDocumentStatusException     если при отправке на обработку у документа неправильный статус.
     * @throws PayloadToJsonProcessingException при ошибке преобразования сообщения в JSON формат.
     */
    @Override
    @Transactional
    public DocumentDto sendToProcessing(IdDto idDto) {
        Optional<Document> documentOptional = repository.findById(idDto.getId());
        if (documentOptional.isEmpty()) {
            throw new DocumentNotFoundException(
                    String.format("Document with id %d not found", idDto.getId()));
        }
        Document document = documentOptional.get();
        if (!document.getStatusCode().equals(StatusEnum.NEW.name())) {
            throw new WrongDocumentStatusException(String.format("Document with id %d has status %s instead of %s",
                    idDto.getId(), document.getStatusCode(), StatusEnum.NEW.name()));
        }

        document.setStatusCode(StatusEnum.IN_PROCESS.name());
        document = repository.save(document);
        log.info("Document with id {} successfully updated it's status to {}",
                document.getId(), document.getStatusCode());
        outboxService.saveMessage(document);
        return mapper.map(document, DocumentDto.class);
    }

    /**
     * Метод для получения списка всех документов.
     *
     * @return Возвращает список всех документов.
     */
    @Override
    @Transactional
    public List<DocumentDto> findAll() {
        List<DocumentDto> dtos =
                repository.findAll().stream()
                        .map(doc -> mapper.map(doc, DocumentDto.class)).collect(Collectors.toList());
        log.info("All documents were received from repository. Documents: {}", dtos);
        return dtos;
    }

    /**
     * Метод для получения документа по его номеру.
     *
     * @param id Номер документа.
     * @return Возвращает документ с номером {@code id}.
     * @throws DocumentNotFoundException если документ не был найден.
     */
    @Override
    @Transactional
    public DocumentDto findById(Long id) {
        DocumentDto documentDto = Optional.of(getById(id))
                .map(doc -> mapper.map(doc, DocumentDto.class)).get();
        log.info("Document with id {} was received from repository. Document: {}",
                documentDto.getId(), documentDto);
        return documentDto;
    }

    /**
     * Метод для запланированного периодического (с фиксированной задержкой) чтения
     * входящих сообщений и обновления статусов документов на основе этих сообщений.
     *
     * @throws WrongDocumentStatusException если у документа неверный текущий или новый статус.
     * @throws ru.documents.service.exception.MessageNotFoundException если при пометке
     * прочитанным сообщение не было найдено.
     */
    @Transactional
    @Scheduled(fixedDelay = 4000)
    public void readInboxAndUpdateDocumentsStatus() {
        List<Inbox> allUnreadMessages = inboxService.getAllUnreadMessages();
        List<Long> allUnreadMessagesMappedToDocumentsIds =
                allUnreadMessages.stream()
                        .map(m -> m.getPayload().getDocumentId())
                        .collect(Collectors.toList());

        List<Document> documentsByIds = repository.findAllById(allUnreadMessagesMappedToDocumentsIds);

        for (int i = 0; i < documentsByIds.size(); i++) {
            Document currentDocument = documentsByIds.get(i);
            Inbox currentMessage = allUnreadMessages.get(i);
            if (areStatusesValid(currentDocument, currentMessage)) {
                documentsByIds.get(i).setStatusCode(currentMessage.getPayload().getStatusCode());
                log.info("Document with id {} received new status: {}",
                        currentDocument.getId(), currentMessage.getPayload().getStatusCode());
            } else {
                inboxService.setMessageAsRead(currentMessage.getId());
                log.info("SAVED READ 2 id: {}", currentMessage.getId());
                throw new WrongDocumentStatusException(
                        String.format("Document with id: %d current status: %s or new status: %s is not valid",
                                currentDocument.getId(), StatusEnum.IN_PROCESS.name(),
                                currentMessage.getPayload().getStatusCode()));
            }
        }

        repository.saveAll(documentsByIds);
        inboxService.setMessagesAsRead(
                allUnreadMessages.stream()
                        .map(Inbox::getId)
                        .collect(Collectors.toList()));
    }

    /**
     * Метод для проверки валидности текущего и нового статусов документа.
     *
     * @param document Документ.
     * @param message Сообщение с новым статусом документа.
     * @return Возвращает true, если статусы валидны, иначе - false.
     */
    private boolean areStatusesValid(Document document, Inbox message) {
        return document.getStatusCode().equals(StatusEnum.IN_PROCESS.name()) &&
                EnumUtils.isValidEnum(StatusEnum.class, message.getPayload().getStatusCode());
    }


    /**
     * Метод для получения документа по его номеру.
     *
     * @param id Номер документа.
     * @return Возвращает документ по его номеру.
     * @throws DocumentNotFoundException если документ не был найден.
     */
    private Document getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DocumentNotFoundException(
                String.format("Document with id %d not found", id)));
    }
}
