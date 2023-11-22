package ru.documents.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;
import ru.documents.controller.dto.IdsDto;
import ru.documents.service.DocumentService;
import ru.documents.service.exception.DocumentFieldsNotValidException;

import javax.validation.Valid;
import java.util.List;

/**
 * REST-контроллер для сущности документа. <br>
 * Реагирует на запросы по пути {@code /documents}.
 *
 * @author Артем Дружинин.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    /**
     * Бин (экземпляр) класса-сервиса для сущности документа.
     */
    private final DocumentService service;

    /**
     * Метод для сохранения нового документа {@link DocumentDto}. <br>
     * Реагирует на POST-запросы по пути {@code /documents}. <br>
     * Принимает запрос и отдаёт ответ в формате JSON.
     *
     * @param dto Документ, который нужно сохранить. <br>
     *            Аннотация {@link Valid} проверяет корректность введённых пользователем полей.
     * @return Возвращает сохраненный документ.
     * @throws DocumentFieldsNotValidException при валидации неверных полей документа.
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@Valid @RequestBody DocumentDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new DocumentFieldsNotValidException("Error validating received document fields");
        }
        return service.save(dto);
    }

    /**
     * Метод для получения списка всех документов {@link DocumentDto}. <br>
     * Реагирует на GET-запросы по пути {@code /documents}. <br>
     * Отдаёт ответ в формате JSON.
     *
     * @return Возвращает список всех документов.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return service.findAll();
    }

    /**
     * Метод для отправки документа {@link DocumentDto} на обработку. <br>
     * Реагирует на POST-запросы по пути {@code /documents/send}. <br>
     * Принимает запрос и отдаёт ответ в формате JSON.
     *
     * @param id Номер документа, который нужно отправить на обработку.
     * @return Возвращает отправленный на обработку документ.
     */
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@RequestBody IdDto id) {
        return service.sendToProcessing(id);
    }

    /**
     * Метод для удаления документа. <br>
     * Реагирует на DELETE-запросы по пути {@code /documents/{id}}. <br>
     *
     * @param id Номер документа, который нужно удалить.
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    /**
     * Метод для удаления всех документов по заданному списку их номеров. <br>
     * Реагирует на DELETE-запросы по пути {@code /documents}. <br>
     *
     * @param idsDto Список номеров документов, которые нужно удалить.
     */
    @DeleteMapping
    public void deleteAll(@RequestBody IdsDto idsDto) {
        service.deleteAll(idsDto.getIds());
    }
}
