package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboxDocumentProcessingResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long documentId;

    @NotBlank
    private String statusCode;
}
