package ru.documents.mapping;

import org.springframework.stereotype.Component;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentMapperImpl implements DocumentMapper{
    @Override
    public Document dtoToModel(DocumentDto documentDto) {
        return Document.builder()
                .id(documentDto.getId())
                .type(documentDto.getType())
                .organization(documentDto.getOrganization())
                .description(documentDto.getDescription())
                .date(documentDto.getDate())
                .patient(documentDto.getPatient())
                .statusCode(documentDto.getStatus().getCode()).build();
    }

    @Override
    public DocumentDto modelToDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .type(document.getType())
                .organization(document.getOrganization())
                .description(document.getDescription())
                .date(document.getDate())
                .patient(document.getPatient())
                .status(Status.of(document.getStatusCode(),
                        StatusEnum.valueOf(document.getStatusCode()).getExtendedName())).build();
    }

    @Override
    public List<DocumentDto> toListDto(List<Document> documents) {
        List<DocumentDto> dtos = new ArrayList<>();
        for (Document document : documents) {
            dtos.add(modelToDto(document));
        }
        return dtos;
    }
}
