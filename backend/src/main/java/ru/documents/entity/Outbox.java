package ru.documents.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность для исходящего сообщения для Kafka.
 *
 * @author Артем Дружинин.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "outbox")
public class Outbox {
    /**
     * Номер сообщения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Основная часть сообщения - информация о документе, который нужно обработать.
     */
    @Column(name = "payload")
    private String payload;

    /**
     * Индикатор того, было ли отправлено сообщение.
     */
    @Column(name = "is_sent")
    private boolean isSent;

    public Outbox(Long id, String payload) {
        this.id = id;
        this.payload = payload;
    }
}
