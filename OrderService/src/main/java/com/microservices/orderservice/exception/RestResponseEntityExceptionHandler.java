package com.microservices.orderservice.exception;

import com.microservices.orderservice.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrderServiceCustomException.class)
    public ResponseEntity<ErrorResponse> handleOrderServiceException(OrderServiceCustomException exception) {
        return new ResponseEntity<>(
                new ErrorResponse().builder()
                .errorCode(exception.getErrorCode())
                .errorMessage(exception.getMessage())
                .build(),
                HttpStatus.valueOf(exception.getStatus()));
    }
}
