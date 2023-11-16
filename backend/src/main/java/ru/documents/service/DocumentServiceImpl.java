package ru.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;
import ru.documents.mapping.DocumentMapper;
import ru.documents.repository.DocumentRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentMapper mapper;

    private final DocumentRepository repository;

    @Override
    public DocumentDto save(DocumentDto documentDto) {
        documentDto.setId(null);
        documentDto.setDate(new Date());
        documentDto.setStatus(Status.of(StatusEnum.NEW.name(),
                StatusEnum.NEW.getExtendedName()));
        Logger.getLogger(DocumentServiceImpl.class.getName())
                .log(Level.INFO, "Saving document: " + documentDto);
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
        repository.delete(getById(documentDto.getId()));
        documentDto.setStatus(Status.of(StatusEnum.IN_PROCESS.name(),
                StatusEnum.IN_PROCESS.getExtendedName()));
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
