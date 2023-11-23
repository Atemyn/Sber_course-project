package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Основной класс Spring.
 */
@SpringBootApplication(scanBasePackages = {
        "ru.*"
})
@EntityScan(basePackages = "ru.documents.entity")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
