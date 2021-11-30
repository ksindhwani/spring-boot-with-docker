package com.example.personio.interview.exceptions;

import org.springframework.http.HttpStatus;

public class LoopHierarchyException extends PersonioCustomException {
  
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public LoopHierarchyException(Throwable ex, Object responseData, String customErrorMsg) {
        super(ex, responseData, customErrorMsg, HTTP_STATUS);
    }
    
}
