package com.example.personio.interview.exceptions;

import org.springframework.http.HttpStatus;

public class UnableToSaveInDbException extends PersonioCustomException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public UnableToSaveInDbException(Throwable ex, Object responseData, String customErrorMsg) {
        super(ex, responseData, customErrorMsg, HTTP_STATUS);
    }
    
}
