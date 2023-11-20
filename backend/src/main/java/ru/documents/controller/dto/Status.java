package ru.documents.controller.dto;

import lombok.Data;

/**
 * Класс для представления статуса документа.
 *
 * @author Артем Дружинин.
 */
@Data
public class Status {
    /**
     * Код статуса документа.
     */
    private String code;

    /**
     * Полное наименование статуса документа.
     */
    private String name;

    /**
     * Статический метод для создания экземпляра класса {@link Status}.
     *
     * @param code Код статуса документа.
     * @param name Полное наименование статуса документа.
     * @return Возвращает экземпляр класса {@link Status}.
     */
    public static Status of(String code, String name) {
        Status codeName = new Status();
        codeName.setCode(code);
        codeName.setName(name);
        return codeName;
    }
}
