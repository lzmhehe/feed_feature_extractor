package com.dishan.ffe.demo;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 8109469326798389194L;
    public static HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    
    public static ServiceException zero_exception = new ServiceException("400","除数不能为0");


    private String errorCode;


    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
