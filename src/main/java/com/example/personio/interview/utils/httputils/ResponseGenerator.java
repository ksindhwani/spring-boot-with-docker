package com.example.personio.interview.utils.httputils;

import com.example.personio.interview.exceptions.PersonioCustomException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ResponseGenerator {

    public Response successResponse(HttpStatus httpStatus) {
        Response response = new Response(true,httpStatus,null,null,null,null);
        return response;
    }

    public Response failedResponse(PersonioCustomException ex) {
        return new Response(
            false,
            ex.getHttpStatus(),
            ex.getErrorMessage(),
            ex.getCustomerErrorMessage(),
            ex.getErrorCause(),
            ex.getResponseData()
        );
    } 
}
