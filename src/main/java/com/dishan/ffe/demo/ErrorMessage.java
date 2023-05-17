package com.dishan.ffe.demo;

import lombok.Data;
import java.io.Serializable;

@Data
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 8065583911104112360L;
    private String errorCode;
    private String errorMessage;

    public ErrorMessage(String errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorMessage() {
        super();
    }
}
