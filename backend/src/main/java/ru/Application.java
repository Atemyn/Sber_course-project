package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages = {
        "ru.*"
})
@EntityScan(basePackages = "ru.documents.entity")
@EnableJpaRepositories(basePackages = "ru.documents.repository")
public class Application {

    public static void main(String[] args) {
        Logger.getLogger(String.valueOf(Application.class)).log(Level.INFO, "Started!!!");
        SpringApplication.run(Application.class, args);
    }
}
