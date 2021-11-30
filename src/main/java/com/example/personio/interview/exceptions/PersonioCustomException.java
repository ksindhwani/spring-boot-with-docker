package com.example.personio.interview.exceptions;

import org.springframework.http.HttpStatus;

public class PersonioCustomException extends RuntimeException {
    private String errorMessage;
    private Object errorCause;
    private Object responseData;
    private String customerErrorMessage;
    private HttpStatus httpStatus;

    public PersonioCustomException(
            Throwable ex,
            Object responseData, 
            String customErrorMsg,
            HttpStatus httpStatus
        ) {
        super(ex);
        this.errorMessage = ex.getMessage();
        this.errorCause = ex.getCause();
        this.responseData = responseData;
        this.customerErrorMessage = customErrorMsg;
        this.httpStatus = httpStatus;

    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getErrorCause() {
        return errorCause;
    }

    public Object getResponseData() {
        return responseData;
    }
    public String getCustomerErrorMessage() {
        return customerErrorMessage;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
