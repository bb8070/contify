package com.example.contify.global.exception;

import com.example.contify.global.error.ErrorCode;

public class ContentNotFoundException extends ApiException{
    public ContentNotFoundException(Long id){
        super(ErrorCode.CONTENT_NOT_FOUND);

    }
}
