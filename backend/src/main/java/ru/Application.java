package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages = {
        "ru.*"
})
public class Application {

    public static void main(String[] args) {
        Logger.getLogger(String.valueOf(Application.class)).log(Level.INFO, "Started!!!");
        SpringApplication.run(Application.class, args);
    }
}
