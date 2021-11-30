package com.example.personio.interview.exceptions;

import org.springframework.http.HttpStatus;

public class PersonioBadRequestException extends PersonioCustomException {
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    
    public PersonioBadRequestException(Throwable ex, Object responseData, String customErrorMsg) {
        super(ex, responseData, customErrorMsg, HTTP_STATUS);
    }
    
}
