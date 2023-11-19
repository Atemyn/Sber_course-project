package ru.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboxDocumentProcessingResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long documentId;

    private String statusCode;
}
