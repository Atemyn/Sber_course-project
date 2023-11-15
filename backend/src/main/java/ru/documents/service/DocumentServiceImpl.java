package ru.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.entity.Document;
import ru.documents.mapping.DocumentMapper;
import ru.documents.repository.DocumentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Primary
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentMapper mapper;

    private final DocumentRepository repository;

    @Override
    public DocumentDto save(DocumentDto documentDto) {
        return mapper.modelToDto(repository.save(
                mapper.dtoToModel(documentDto)));
    }

    @Override
    public void deleteAll(Set<Long> ids) {
        for (Long id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteById(Long id) {
        var document = getById(id);
        repository.delete(document);
    }

    @Override
    public DocumentDto update(DocumentDto documentDto) {
        var existingDocument = getById(documentDto.getId());
        repository.delete(existingDocument);
        return mapper.modelToDto(repository.save(mapper.dtoToModel(documentDto)));
    }

    @Override
    public List<DocumentDto> findAll() {
        return mapper.toListDto(repository.findAll());
    }

    @Override
    public DocumentDto findById(Long id) {
        return Optional.of(getById(id)).map(mapper::modelToDto).get();
    }

    private Document getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(
                "Book with id: " + id + " not found"));
    }
}
