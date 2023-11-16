package ru.documents.controller.dto;

public enum StatusEnum {
    NEW("Новый"),
    IN_PROCESS("В обработке"),
    ACCEPTED("Принят"),
    REJECTED("Отклонен");

    private final String extendedName;

    StatusEnum(String extendedName) {
        this.extendedName = extendedName;
    }

    public String getExtendedName() {
        return extendedName;
    }
}
