package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Класс для представления документа в клиентской части.
 *
 * @author Артем Дружинин.
 */
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
    @Length(max = 127, message = "Наименование документа не может быть длиннее 127 символов")
    private String type;

    /**
     * Организация.
     */
    @NotBlank(message = "Название организации не может быть пустым")
    @Length(max = 255, message = "Название организации не может быть длиннее 255 символов")
    private String organization;

    /**
     * Описание.
     */
    @NotBlank(message = "Описание документа не может быть пустым")
    @Length(max = 512, message = "Описание документа не может быть длиннее 512 символов")
    private String description;

    /**
     * Дата документа.
     */
    private Date date;

    /**
     * Пациент.
     */
    @NotBlank(message = "Имя пациента не может быть пустым")
    @Length(max = 255, message = "Имя пациента не может быть длиннее 255 символов")
    private String patient;

    /**
     * Статус.
     */
    private Status status;
}
