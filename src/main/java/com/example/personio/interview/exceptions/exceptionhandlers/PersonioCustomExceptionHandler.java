package com.example.personio.interview.exceptions.exceptionhandlers;

import com.example.personio.interview.exceptions.PersonioCustomException;
import com.example.personio.interview.utils.httputils.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class PersonioCustomExceptionHandler extends ResponseEntityExceptionHandler{
    
    @ExceptionHandler(PersonioCustomException.class)
    public final ResponseEntity<?> handlePersonioCustomExceptions(PersonioCustomException ex) 
    {
        Response exceptionResponse = new Response(
            false,
            ex.getHttpStatus(),
            ex.getErrorMessage(),
            ex.getCustomerErrorMessage(),
            ex.getErrorCause(),
            ex.getResponseData());
        return new ResponseEntity<>(exceptionResponse, ex.getHttpStatus());
    }    
}
