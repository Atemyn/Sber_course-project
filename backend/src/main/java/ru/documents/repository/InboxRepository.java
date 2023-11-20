package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Inbox;

import java.util.List;

/**
 * Интерфейс JPA репозитория для сущности {@link Inbox} для
 * осуществления действий с этой сущностью в базе данных.
 *
 * @author Артем Дружинин.
 */
public interface InboxRepository extends JpaRepository<Inbox, Long> {
    /**
     * Метод для получения всех экземпляров сущности по полю {@code isRead}.
     *
     * @param isRead Поле, по которому нужно сделать выборку.
     * @return Возвращает список экземпляров сущности {@link Inbox},
     * у которых поле {@code isRead} равно переданному значению.
     */
    List<Inbox> findAllByIsRead(boolean isRead);
}
