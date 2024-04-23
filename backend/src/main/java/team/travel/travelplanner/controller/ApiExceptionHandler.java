package team.travel.travelplanner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import team.travel.travelplanner.exception.ApiException;
import team.travel.travelplanner.model.error.ApiErrorModel;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ApiErrorModel> handleApiException(ApiException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorModel(ex.getType(), ex.getMessage()));
    }
}
