package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    /**
     * Номер документа.
     */
    private Long id;

    /**
     * Вид документа.
     */
    private String type;

    /**
     * Организация.
     */
    private String organization;

    /**
     * Описание.
     */
    private String description;

    /**
     * Дата документа.
     */
    private Date date;

    /**
     * Пациент.
     */
    private String patient;

    /**
     * Статус.
     */
    private Status status;
}
