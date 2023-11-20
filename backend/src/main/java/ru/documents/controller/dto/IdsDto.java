package ru.documents.controller.dto;

import lombok.Data;

import java.util.Set;

/**
 * Класс для представления множества номеров документов.
 *
 * @author Артем Дружинин.
 */
@Data
public class IdsDto {
    /**
     * Множество номеров документов.
     */
    private Set<Long> ids;
}
