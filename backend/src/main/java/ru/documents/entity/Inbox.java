package ru.documents.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.documents.controller.dto.InboxDocumentProcessingResult;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность для входящего сообщения от Kafka.
 *
 * @author Артем Дружинин.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "inbox")
public class Inbox {
    /**
     * Номер сообщения.
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * Основная часть сообщения - результат обработки документа.
     */
    @Column(name = "payload")
    private InboxDocumentProcessingResult payload;

    /**
     * Индикатор того, было ли прочитано сообщение.
     */
    @Column(name = "is_read")
    private boolean isRead;

    public Inbox(Long id, InboxDocumentProcessingResult payload) {
        this.id = id;
        this.payload = payload;
    }
}
