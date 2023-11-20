package ru.documents.service;

import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс сервиса по работе с документами.
 *
 * @author Артем Дружинин.
 */
public interface DocumentService {
    /**
     * Метод для сохранения документа.
     *
     * @param documentDto Документ, который нужно сохранить.
     * @return Возвращает сохраненный документ.
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Метод для удаления всех документов по списку их номеров.
     *
     * @param ids Номера документов, которые нужно удалить.
     */
    void deleteAll(Set<Long> ids);

    /**
     * Метод для удаления документа по его номеру.
     *
     * @param id Номер документа, который нужно удалить.
     */
    void deleteById(Long id);

    /**
     * Метод для отправки документа на обработку по его номеру.
     *
     * @param idDto Номер документа.
     * @return Возвращает отправленный на обработку документ.
     */
    DocumentDto sendToProcessing(IdDto idDto);

    /**
     * Метод для получения списка всех документов.
     *
     * @return Возвращает список всех документов.
     */
    List<DocumentDto> findAll();

    /**
     * Метод для получения документа по его номеру.
     *
     * @param id Номер документа.
     * @return Возвращает документ с номером {@code id}.
     */
    DocumentDto findById(Long id);
}
