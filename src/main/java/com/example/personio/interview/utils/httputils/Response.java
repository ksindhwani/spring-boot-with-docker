package com.example.personio.interview.utils.httputils;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.http.HttpStatus;

public class Response {
    // Changing Time Stamp to readable format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private boolean sucesss;
    private HttpStatus httpStatus;
    private String errMessage;
    private String customErrMessage;
    private Object errCause;
    private Object data;

    public Response(
        boolean success,
        HttpStatus httpStatus,
        String errMessage,
        String customErrMessage,
        Object errCause,
        Object data
    ) {
        timestamp = new Date();
        this.sucesss = success;
        this.httpStatus = httpStatus;
        this.errMessage = errMessage;
        this.customErrMessage = customErrMessage;
        this.errCause = errCause;
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isSucesss() {
        return sucesss;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public String getCustomErrMessage() {
        return customErrMessage;
    }

    public Object getErrCause() {
        return errCause;
    }
    
    public Object getData() {
        return data;
    }

}