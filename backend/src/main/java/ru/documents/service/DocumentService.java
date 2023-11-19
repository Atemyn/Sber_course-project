package ru.documents.service;

import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохранить документ
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Удалить документ
     * @param ids идентификаторы документов
     */
    void deleteAll(Set<Long> ids);

    /**
     * Удалить документ по ид
     * @param id идентификатор документа
     */
    void deleteById(Long id);

    /**
     * Обновить документ
     * @param idDto Номер документа
     * @return обновленный документ
     */
    DocumentDto update(IdDto idDto);

    /**
     * Получить все документы
     * @return список документов
     */
    List<DocumentDto> findAll();

    /**
     * Получить документ по номеру
     * @param id идентификатор
     * @return документ
     */
    DocumentDto findById(Long id);
}
