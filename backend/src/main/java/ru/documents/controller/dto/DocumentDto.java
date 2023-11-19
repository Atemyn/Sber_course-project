package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
     * Вид/наименование документа.
     */
    @NotBlank(message = "Наименование документа не может быть пустым")
    private String type;

    /**
     * Организация.
     */
    @NotBlank(message = "Название организации не может быть пустым")
    private String organization;

    /**
     * Описание.
     */
    @NotBlank(message = "Описание документа не может быть пустым")
    private String description;

    /**
     * Дата документа.
     */
    private Date date;

    /**
     * Пациент.
     */
    @NotBlank(message = "Имя пациента не может быть пустым")
    private String patient;

    /**
     * Статус.
     */
    private Status status;
}
