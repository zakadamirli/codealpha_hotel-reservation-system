package com.zekademirli.hostify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PropertyValidationException extends RuntimeException {
    public PropertyValidationException(String message) {
        super(message);
    }
}
