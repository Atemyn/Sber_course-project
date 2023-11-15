package ru.documents.service;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.Status;
import ru.documents.store.DocumentStore;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//@Service
public class DocumentServiceStoreImpl {

    public DocumentDto save(DocumentDto documentDto) {
        if (documentDto.getId() == null) {
            documentDto.setId(RandomUtils.nextLong(0L, 999L));
        }
        documentDto.setDate(new Date());
        if (documentDto.getStatus() == null) {
            documentDto.setStatus(Status.of("NEW", "Новый"));
        }
        DocumentStore.getInstance().getDocumentDtos().add(documentDto);
        return documentDto;
    }


    public DocumentDto update(DocumentDto documentDto) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        Optional<DocumentDto> dto = documentDtos.stream()
                .filter(d -> d.getId().equals(documentDto.getId())).findFirst();
        if (dto.isPresent()) {
            deleteById(documentDto.getId());
            save(documentDto);
        }
        return documentDto;
    }

    public void deleteById(Long id) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !d.getId().equals(id)).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
    }

    public void deleteAll(Set<Long> ids) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !ids.contains(d.getId())).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
    }

    public List<DocumentDto> findAll() {
        return DocumentStore.getInstance().getDocumentDtos();
    }

    public DocumentDto findById(Long id) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        return documentDtos.stream()
                .filter(d -> d.getId().equals(id)).findFirst().get();
    }
}
