package ru.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Класс конфигурации для включения выполнения методов с аннотацией <br>
 * {@link org.springframework.scheduling.annotation.Scheduled} по расписанию <br>
 * (через заданные промежутки времени).
 *
 * @author Артем Дружинин.
 */
@Configuration
@EnableScheduling
public class SchedulerConfiguration {
}
