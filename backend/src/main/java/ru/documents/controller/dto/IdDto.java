package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для представления номера документа.
 *
 * @author Артем Дружинин.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdDto {
    /**
     * Номер документа.
     */
    private Long id;
}
