package ru.documents.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document {
    /**
     * Номер документа.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Вид документа.
     */
    @Column(name = "doc_type", nullable = false)
    private String type;

    /**
     * Организация.
     */
    @Column(name = "organization", nullable = false)
    private String organization;

    /**
     * Описание.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Дата документа.
     */
    @Column(name = "creation_date", nullable = false)
    private Date date;

    /**
     * Пациент.
     */
    @Column(name = "patient", nullable = false)
    private String patient;

    /**
     * Статус.
     */
    @Column(name = "status", nullable = false)
    private String status;
}
