package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * Класс для представления результатов обработки документа.
 *
 * @author Артем Дружинин.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboxDocumentProcessingResult implements Serializable {
    /**
     * Версия класса, реализующего интерфейс {@link Serializable}, для проверки,
     * что загруженный класс и сериализованный объект совместимы.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Номер обработанного документа.
     */
    @NotNull
    private Long documentId;

    /**
     * Код статуса обработанного документа.
     */
    @NotBlank
    private String statusCode;
}
