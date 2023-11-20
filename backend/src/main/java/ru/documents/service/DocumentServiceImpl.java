package ru.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;
import ru.documents.entity.Inbox;
import ru.documents.mapping.DocumentMapper;
import ru.documents.repository.DocumentRepository;
import ru.documents.service.exception.DocumentNotFoundException;
import ru.documents.service.exception.PayloadToJsonProcessingException;
import ru.documents.service.exception.WrongDocumentStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final DocumentMapper mapper;

    private final DocumentRepository repository;

    private final OutboxService outboxService;

    private final InboxService inboxService;

    @Override
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        documentDto.setId(null);
        documentDto.setDate(new Date());
        documentDto.setStatus(Status.of(StatusEnum.NEW.name(),
                StatusEnum.NEW.getExtendedName()));
        documentDto = mapper.modelToDto(repository.save(
                mapper.dtoToModel(documentDto)));
        log.info("Document was successfully saved. Document: " + documentDto);
        return documentDto;
    }

    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        for (Long id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var document = getById(id);
        repository.delete(document);
        log.info(String.format("Document with id %d was successfully deleted", id));
    }

    @Override
    @Transactional
    public DocumentDto update(IdDto idDto) {
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
        log.info(String.format("Document with id %d successfully updated it's status to %s",
                document.getId(), document.getStatusCode()));
        try {
            outboxService.saveMessage(document);
        } catch (PayloadToJsonProcessingException e) {
            log.warn(e.getMessage());
        }
        return mapper.modelToDto(document);
    }

    @Override
    @Transactional
    public List<DocumentDto> findAll() {
        List<DocumentDto> dtos = mapper.toListDto(repository.findAll());
        log.info(String.format("All documents were received from repository. Documents: %s", dtos));
        return dtos;
    }

    @Override
    @Transactional
    public DocumentDto findById(Long id) {
        DocumentDto documentDto = Optional.of(getById(id)).map(mapper::modelToDto).get();
        log.info(String.format("Document with id %d was received from repository. Document: %s",
                documentDto.getId(), documentDto));
        return documentDto;
    }

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
            if (currentDocument.getStatusCode().equals(StatusEnum.IN_PROCESS.name())) {
                documentsByIds.get(i).setStatusCode(allUnreadMessages.get(i).getPayload().getStatusCode());
                log.info(String.format("Document with id %d received new status: %s",
                        currentDocument.getId(), allUnreadMessages.get(i).getPayload().getStatusCode()));
            }
            else {
                log.warn(String.format("Document with id %d doesn't have a status %s so it can't be processed",
                        currentDocument.getId(), StatusEnum.IN_PROCESS.name()));
            }
        }

        repository.saveAll(documentsByIds);
        inboxService.setMessagesAsRead(
                allUnreadMessages.stream()
                .map(Inbox::getId)
                .collect(Collectors.toList()));
    }

    private Document getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DocumentNotFoundException(
                String.format("Document with id %d not found", id)));
    }
}
