package ru.documents.mapping;

import org.springframework.stereotype.Component;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.entity.Document;

import java.util.List;

public interface DocumentMapper {
    Document dtoToModel(DocumentDto documentDto);

    DocumentDto modelToDto(Document document);

    List<DocumentDto> toListDto(List<Document> documents);
}
