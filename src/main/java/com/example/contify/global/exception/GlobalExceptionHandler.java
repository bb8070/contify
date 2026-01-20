package com.example.contify.global.exception;

import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handle(ApiException e){
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.badRequest().body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>>handleValidation(){
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INVALID_REQUEST));
    }

}
