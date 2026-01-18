package com.example.contify.global.exception;

import com.example.contify.global.error.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handle(ApiException e){
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
