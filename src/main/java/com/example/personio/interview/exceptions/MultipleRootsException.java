package com.example.personio.interview.exceptions;

import org.springframework.http.HttpStatus;

public class MultipleRootsException extends PersonioCustomException {
    public static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public MultipleRootsException(Throwable ex, Object responseData, String customErrorMsg) {
        super(ex, responseData, customErrorMsg,httpStatus);
    }
}
