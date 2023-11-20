package ru.documents.controller.dto;

/**
 * Перечисление всех видов статуса документа.
 *
 * @author Артем Дружинин.
 */
public enum StatusEnum {
    NEW("Новый"),
    IN_PROCESS("В обработке"),
    ACCEPTED("Принят"),
    REJECTED("Отклонен");

    /**
     * Полное наименование статуса документа.
     */
    private final String extendedName;

    StatusEnum(String extendedName) {
        this.extendedName = extendedName;
    }

    public String getExtendedName() {
        return extendedName;
    }
}
