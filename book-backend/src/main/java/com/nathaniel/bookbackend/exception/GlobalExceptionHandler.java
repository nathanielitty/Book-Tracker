package com.nathaniel.bookbackend.exception;

import com.nathaniel.bookbackend.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Global exception handler following Netflix standards
 * Provides consistent error responses across the application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Business exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getUserMessage())
                        .details(ex.getMessage())
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Validation exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("VALIDATION_FAILED")
                        .message("Validation failed for one or more fields")
                        .details("Please check the field errors for more details")
                        .fieldErrors(fieldErrors)
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Constraint violation exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        Map<String, List<String>> fieldErrors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("CONSTRAINT_VIOLATION")
                        .message("Constraint validation failed")
                        .details("One or more constraints were violated")
                        .fieldErrors(fieldErrors)
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Authentication exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("AUTHENTICATION_FAILED")
                        .message("Authentication failed")
                        .details("Invalid credentials provided")
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Access denied exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("ACCESS_DENIED")
                        .message("Access denied")
                        .details("You don't have permission to access this resource")
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Method not supported exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("METHOD_NOT_SUPPORTED")
                        .message("HTTP method not supported")
                        .details(String.format("Method '%s' is not supported for this endpoint", ex.getMethod()))
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("No handler found exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("ENDPOINT_NOT_FOUND")
                        .message("Endpoint not found")
                        .details(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("HTTP message not readable exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("MALFORMED_REQUEST")
                        .message("Malformed request body")
                        .details("The request body could not be parsed. Please check your JSON format.")
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Method argument type mismatch exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("INVALID_PARAMETER_TYPE")
                        .message("Invalid parameter type")
                        .details(String.format("Parameter '%s' should be of type %s", ex.getName(), ex.getRequiredType().getSimpleName()))
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("Missing servlet request parameter exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI());
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("MISSING_PARAMETER")
                        .message("Missing required parameter")
                        .details(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Unexpected exception occurred: {} | TraceId: {} | Path: {}", ex.getMessage(), traceId, request.getRequestURI(), ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(ApiResponse.ErrorDetails.builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred")
                        .details("Please try again later or contact support if the problem persists")
                        .traceId(traceId)
                        .build())
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
