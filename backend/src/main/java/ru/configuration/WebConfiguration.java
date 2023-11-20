package ru.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Класс для веб-конфигурации.
 *
 * @author Артем Дружинин.
 */
@Configuration
@ConditionalOnProperty(value = "cors.allow", havingValue = "true")
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * Настройка CORS-политики.
     *
     * @param registry Реестр CORS для регистрации конфигурации CORS-политики.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowCredentials(true);
    }
}
