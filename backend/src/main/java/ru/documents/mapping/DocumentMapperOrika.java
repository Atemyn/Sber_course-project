package ru.documents.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.entity.Document;
import ru.documents.service.exception.WrongDocumentStatusException;

/**
 * Маппер для {@link Document} и {@link DocumentDto}.
 *
 * @author Артем Дружинин.
 */
@Component
public class DocumentMapperOrika extends ConfigurableMapper {
    /**
     * Метод для конфигурации фабрики мапперов.
     *
     * @param factory Фабрика мапперов.
     */
    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(DocumentDto.class, Document.class)
                .exclude("status")
                .exclude("statusCode")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DocumentDto documentDto, Document document, MappingContext context) {
                        document.setStatusCode(documentDto.getStatus().getCode());
                    }

                    @Override
                    public void mapBtoA(Document document, DocumentDto documentDto, MappingContext context) {
                        try {
                            documentDto.setStatus(Status.of(document.getStatusCode(),
                                    StatusEnum.valueOf(document.getStatusCode()).getExtendedName()));
                        } catch (IllegalArgumentException e) {
                            throw new WrongDocumentStatusException("Error when mapping Document." +
                                    "Wrong status code: " + document.getStatusCode(), e);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
