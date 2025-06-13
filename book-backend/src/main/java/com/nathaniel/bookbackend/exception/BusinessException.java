package com.nathaniel.bookbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all business exceptions in the application
 * Follows Netflix error handling patterns
 */
@Getter
public abstract class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String userMessage;
    
    protected BusinessException(String errorCode, String message, String userMessage, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
    
    protected BusinessException(String errorCode, String message, String userMessage, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
}
