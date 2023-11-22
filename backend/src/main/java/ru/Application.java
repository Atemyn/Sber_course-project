package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Основной класс Spring.
 */
@SpringBootApplication(scanBasePackages = {
        "ru.*"
})
@EntityScan(basePackages = "ru.documents.entity")
//@EnableJpaRepositories(basePackages = "ru.documents.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
