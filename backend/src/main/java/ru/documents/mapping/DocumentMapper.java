package ru.documents.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.entity.Document;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {
    Document dtoToModel(DocumentDto documentDto);

    DocumentDto modelToDto(Document document);

    List<DocumentDto> toListDto(List<Document> documents);
}
