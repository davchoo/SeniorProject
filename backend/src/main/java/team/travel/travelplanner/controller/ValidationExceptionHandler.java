package team.travel.travelplanner.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import team.travel.travelplanner.model.error.ValidationErrorModel;
import team.travel.travelplanner.model.error.ValueErrorModel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
public class ValidationExceptionHandler  extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    public ValidationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull HttpHeaders headers, @Nonnull HttpStatusCode status, @Nonnull WebRequest request) {
        List<String> globalErrors = ex.getGlobalErrors()
                .stream()
                .map(this::getMessage)
                .toList();
        List<ValueErrorModel> valueErrors = ex.getFieldErrors()
                .stream()
                .map(error -> new ValueErrorModel(
                        error.getField(),
                        convertValue(error.getRejectedValue()),
                        getMessage(error)
                ))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ValidationErrorModel(globalErrors, valueErrors));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, @Nonnull HttpHeaders headers, @Nonnull HttpStatusCode status, @Nonnull WebRequest request) {
        List<ValueErrorModel> valueErrors = new ArrayList<>();
        for (ParameterValidationResult result : ex.getAllValidationResults()) {
            String path = result.getMethodParameter().getParameterName();
            if (result.getContainerIndex() != null) {
                path += "[" + result.getContainerIndex() + "]";
            } else if (result.getContainerKey() != null) {
                path += "[\"" + result.getContainerKey() + "\"]";
            }
            String value = convertValue(result.getArgument());
            for (MessageSourceResolvable resolvableError : result.getResolvableErrors()) {
                valueErrors.add(new ValueErrorModel(path, value, getMessage(resolvableError)));
            }
        }
        return ResponseEntity.badRequest()
                .body(new ValidationErrorModel(List.of(), valueErrors));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            List<ValueErrorModel> valueErrors = List.of(new ValueErrorModel(
                    invalidFormatException.getPathReference(),
                    convertValue(invalidFormatException.getValue()),
                    invalidFormatException.getMessage()
            ));
            return ResponseEntity.badRequest()
                    .body(new ValidationErrorModel(List.of(), valueErrors));
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private String convertValue(Object value) {
        String str = Objects.toString(value);
        if (value instanceof String) {
            str = "\"" + str + "\"";
        }
        return str;
    }

    private String getMessage(MessageSourceResolvable resolvable) {
        String message = "Message not found";
        try {
            message = messageSource.getMessage(resolvable, Locale.ENGLISH);
        } catch (NoSuchMessageException ignored) {}
        return message;
    }
}
