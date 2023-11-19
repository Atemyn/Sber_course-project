package ru.documents.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.documents.controller.dto.InboxDocumentProcessingResult;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "inbox")
public class Inbox {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "payload")
    private InboxDocumentProcessingResult payload;

    @Column(name = "is_read")
    private boolean isRead;

    public Inbox(Long id, InboxDocumentProcessingResult payload) {
        this.id = id;
        this.payload = payload;
    }
}
