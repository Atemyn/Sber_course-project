package ru.documents.controller.dto;

import lombok.Data;

import java.util.Set;

@Data
public class IdsDto {
    /**
     * Множество номеров документов.
     */
    private Set<Long> ids;

}
