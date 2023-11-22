package ru.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.documents.service.exception.DocumentFieldsNotValidException;
import ru.documents.service.exception.DocumentNotFoundException;
import ru.documents.service.exception.InboxDuplicateSaveAttemptException;
import ru.documents.service.exception.PayloadToJsonProcessingException;
import ru.documents.service.exception.WrongDocumentStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Обработчик REST API исключений.
 *
 * @author Артем Дружинин.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * Обработчик ошибки валидации (неверных аргументов метода).
     *
     * @param ex      Исключение об ошибке валидации аргументов метода.
     * @param headers HTTP-заголовки, которые будут записаны в HTTP-ответ.
     * @param status  Статус HTTP-ответа.
     * @param request Текущий веб-запрос.
     * @return Возвращает ответ-представление о плохом запросе.
     */
    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull final MethodArgumentNotValidException ex,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatus status,
                                                                  @NonNull final WebRequest request) {
        logger.error("Validation error", ex);

        List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        RestApiError restApiError = new RestApiError("Validation failed", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }

    /**
     * Обработчик исключения, выбрасываемого при валидации неверных полей документа.
     *
     * @param ex Исключение о некорректных полях документа.
     * @return Возвращает ответ-представление о плохом запросе.
     */
    @ExceptionHandler({DocumentFieldsNotValidException.class})
    public ResponseEntity<RestApiError> handleDocumentFieldsNotValidException(
            final DocumentFieldsNotValidException ex) {
        logger.error("Document has invalid fields", ex);
        RestApiError restApiError =
                new RestApiError("Document has invalid fields",
                        List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), BAD_REQUEST);
    }

    /**
     * Обработчик нечитаемого HTTP сообщения.
     *
     * @param ex      Исключение о нечитаемом HTTP сообщении.
     * @param headers HTTP-заголовки, которые будут записаны в HTTP-ответ.
     * @param status  Статус HTTP-ответа.
     * @param request Текущий веб-запрос.
     * @return Возвращает ответ-представление о плохом запросе.
     */
    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        logger.error("Http message is not readable", ex);
        List<String> errors = List.of(ex.getRootCause() ==
                null ? Objects.requireNonNull(ex.getMessage()) : ex.getRootCause().getMessage());
        RestApiError restApiError = new RestApiError("Http message is not readable", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }

    /**
     * Обработчик исключения DocumentNotFoundException.
     *
     * @param ex Вызванное исключение.
     * @return Возвращает ответ-представление об ошибке на сервере.
     */
    @ExceptionHandler({DocumentNotFoundException.class})
    public ResponseEntity<RestApiError> handleDocumentNotFoundException(final DocumentNotFoundException ex) {
        logger.error("Document not found exception", ex);
        RestApiError restApiError =
                new RestApiError("Document not found exception",
                        List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Обработчик исключения WrongDocumentStatusException.
     *
     * @param ex Вызванное исключение.
     * @return Возвращает ответ-представление об ошибке на сервере.
     */
    @ExceptionHandler({WrongDocumentStatusException.class})
    public ResponseEntity<RestApiError> handleWrongDocumentStatusException(final WrongDocumentStatusException ex) {
        logger.error("Document has wrong status", ex);
        RestApiError restApiError =
                new RestApiError("Document has wrong status",
                        List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Обработчик исключения InboxDuplicateSaveAttemptException.
     *
     * @param ex Вызванное исключение.
     * @return Возвращает ответ-представление об ошибке на сервере.
     */
    @ExceptionHandler({InboxDuplicateSaveAttemptException.class})
    public ResponseEntity<RestApiError> handleInboxDuplicateSaveAttemptException(
            final InboxDuplicateSaveAttemptException ex) {
        logger.error("Error when trying to save duplicate kafka message", ex);
        RestApiError restApiError =
                new RestApiError("Error when trying to save duplicate kafka message",
                        List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Обработчик исключения PayloadToJsonProcessingException.
     *
     * @param ex Вызванное исключение.
     * @return Возвращает ответ-представление об ошибке на сервере.
     */
    @ExceptionHandler({PayloadToJsonProcessingException.class})
    public ResponseEntity<RestApiError> handlePayloadToJsonProcessingException(
            final PayloadToJsonProcessingException ex) {
        logger.error("Error when processing object to json format", ex);
        RestApiError restApiError =
                new RestApiError("Error when processing object %s to json format",
                        List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }


    /**
     * Обработчик остальных исключений на сервере.
     *
     * @param ex      Вызванное исключение.
     * @param request Веб-запрос.
     * @return Возвращает ответ-представление об ошибке на сервере.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<RestApiError> handleOtherExceptions(final Exception ex, final WebRequest request) {
        logger.error("Internal server error", ex);
        RestApiError restApiError =
                new RestApiError("Internal server error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Класс для представления ошибки REST API.
     *
     * @author Артем Дружинин.
     */
    @Data
    @AllArgsConstructor
    public static class RestApiError {
        /**
         * Сообщение об ошибке.
         */
        private String message;

        /**
         * Список более развернутых сообщений об ошибке.
         */
        private List<String> errors;
    }
}
