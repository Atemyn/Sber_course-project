package ru.documents.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Document {
    /**
     * Номер документа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Вид документа.
     */
    @Column(nullable = false)
    private String type;

    /**
     * Организация.
     */
    @Column(nullable = false)
    private String organization;

    /**
     * Описание.
     */
    @Column(nullable = false)
    private String description;

    /**
     * Дата документа.
     */
    @Column(nullable = false)
    private Date date;

    /**
     * Пациент.
     */
    @Column(nullable = false)
    private String patient;

    /**
     * Статус.
     */
    @Column(nullable = false)
    private String status;
}
