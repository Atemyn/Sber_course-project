package ru.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.documents.entity.Inbox;
import ru.documents.entity.Outbox;

import java.util.List;

/**
 * Интерфейс JPA репозитория для сущности {@link Outbox} для
 * осуществления действий с этой сущностью в базе данных.
 *
 * @author Артем Дружинин.
 */
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    /**
     * Метод для получения всех экземпляров сущности по полю {@code isSent}.
     *
     * @param isSent Поле, по которому нужно сделать выборку.
     * @return Возвращает список экземпляров сущности {@link Inbox},
     * у которых поле {@code isSent} равно переданному значению.
     */
    List<Outbox> findAllByIsSent(boolean isSent);
}
