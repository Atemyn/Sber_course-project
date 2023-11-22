package ru.documents.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.documents.controller.dto.IdDto;
import ru.documents.controller.dto.IdsDto;
import ru.documents.controller.dto.Status;
import ru.documents.controller.dto.StatusEnum;
import ru.documents.service.DocumentServiceImpl;
import ru.documents.service.exception.DocumentNotFoundException;
import ru.documents.service.exception.WrongDocumentStatusException;
import ru.template.example.controller.AbstractWebMvcTest;
import ru.documents.controller.dto.DocumentDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DocumentControllerTest extends AbstractWebMvcTest {
    private static final String DOCUMENTS_ROUTE = "/documents";

    private static final String DOCUMENTS_SEND_ROUTE = DOCUMENTS_ROUTE + "/send";

    private static final String DOCUMENTS_DELETE_ROUTE = DOCUMENTS_ROUTE + "/{id}";

    @MockBean
    private DocumentServiceImpl service;

    @Test
    @DisplayName("Тест на успешное сохранение документа, когда данные о документе корректны")
    public void testSuccessfulSaveWhenDocumentIsValid() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                null,
                "type",
                "organization",
                "description",
                null,
                "patient",
                null);

        DocumentDto savedDocumentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                Status.of(StatusEnum.NEW.name(), StatusEnum.NEW.getExtendedName()));

        when(service.save(documentDto)).thenReturn(savedDocumentDto);

        mvc.perform(postAction(DOCUMENTS_ROUTE, documentDto))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(savedDocumentDto)));
    }

    @Test
    @DisplayName("Тест на получение 4xx статуса запроса при попытке добавления документа с полем, равным null")
    public void testBadRequestWhenDocumentFieldIsNull() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                null,
                "type",
                "organization",
                "description",
                null,
                null, // Некорректное поле.
                null);

        mvc.perform(postAction(DOCUMENTS_ROUTE, documentDto)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест на получение 4xx статуса запроса при попытке добавления документа с полем," +
            " превышающим максимальную длину для этого поля")
    public void testBadRequestWhenDocumentFieldIsMoreThanMaxLength() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                null,
                "type",
                "organization",
                RandomStringUtils.random(513), // Некорректное поле - его максимальная длина: 512.
                null,
                "patient",
                null);

        mvc.perform(postAction(DOCUMENTS_ROUTE, documentDto)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест на успешное получение списка всех документов")
    public void testSuccessfulGetAllDocuments() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                Status.of(StatusEnum.NEW.name(), StatusEnum.NEW.getExtendedName())
        );

        when(service.findAll()).thenReturn(List.of(documentDto));

        mvc.perform(get(DOCUMENTS_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(documentDto))));
    }

    @Test
    @DisplayName("Тест на успешную отправку существующего документа со статусом НОВЫЙ на обработку")
    public void testSuccessfulSendDocumentsThatExistsAndHasNewStatus() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                Status.of(StatusEnum.IN_PROCESS.name(), StatusEnum.IN_PROCESS.getExtendedName())
        );

        IdDto idDto = new IdDto(1L);

        when(service.sendToProcessing(idDto)).thenReturn(documentDto);

        mvc.perform(postAction(DOCUMENTS_SEND_ROUTE, idDto))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(documentDto)));
    }

    @Test
    @DisplayName("Тест на ошибку сервера при отправке id несуществующего документа")
    public void testInternalServerErrorWhenSendingIdOfNonExistingDocument() throws Exception {
        IdDto idDto = new IdDto(1L);

        when(service.sendToProcessing(idDto)).thenThrow(
                new DocumentNotFoundException("Error finding document with id " + idDto.getId()));

        mvc.perform(postAction(DOCUMENTS_SEND_ROUTE, idDto))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Тест на ошибку сервера при отправке id документа с неверным статусом")
    public void testInternalServerErrorWhenSendingDocumentHasWrongStatus() throws Exception {
        IdDto idDto = new IdDto(1L);

        when(service.sendToProcessing(idDto)).thenThrow(
                new WrongDocumentStatusException(
                        String.format("Document with id %d has wrong status", idDto.getId())));

        mvc.perform(postAction(DOCUMENTS_SEND_ROUTE, idDto))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Тест на успешное удаление документа по id")
    public void testSuccessfulDeleteDocumentById() throws Exception {
        Long id = 1L;
        doNothing().when(service).deleteById(id);
        mvc.perform(delete(DOCUMENTS_DELETE_ROUTE, id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное удаление документов по множеству id")
    public void testSuccessfulDeleteAllDocumentsWithinSetOfIds() throws Exception {
        IdsDto idsDto = new IdsDto();
        idsDto.setIds(Set.of(1L, 2L, 3L));
        doNothing().when(service).deleteAll(idsDto.getIds());
        mvc.perform(deleteAction(DOCUMENTS_ROUTE, idsDto))
                .andExpect(status().isOk());
    }
}
