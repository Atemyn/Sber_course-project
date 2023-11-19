package ru.documents.service;

import lombok.RequiredArgsConstructor;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        return mapper.modelToDto(repository.save(
                mapper.dtoToModel(documentDto)));
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
    }

    @Override
    @Transactional
    public DocumentDto update(IdDto idDto) {
        Optional<Document> documentOptional = repository.findById(idDto.getId());
        if (documentOptional.isEmpty()) {
            throw new RuntimeException();
        }
        Document document = documentOptional.get();
        if (!document.getStatusCode().equals(StatusEnum.NEW.name())) {
            throw new RuntimeException();
        }

        document.setStatusCode(StatusEnum.IN_PROCESS.name());
        document = repository.save(document);
        outboxService.saveMessage(document);
        return mapper.modelToDto(document);
    }

    @Override
    @Transactional
    public List<DocumentDto> findAll() {
        return mapper.toListDto(repository.findAll());
    }

    @Override
    @Transactional
    public DocumentDto findById(Long id) {
        return Optional.of(getById(id)).map(mapper::modelToDto).get();
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
            }
        }

        repository.saveAll(documentsByIds);
        inboxService.setMessagesAsRead(
                allUnreadMessages.stream()
                .map(Inbox::getId)
                .collect(Collectors.toList()));
    }

    private Document getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(
                "Book with id: " + id + " not found"));
    }
}
