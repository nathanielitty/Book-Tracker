package com.nathaniel.bookbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standardized API response wrapper following Netflix/industry standards
 * Provides consistent response structure across all endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    @JsonProperty("success")
    @Builder.Default
    private Boolean success = true;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("error")
    private ErrorDetails error;
    
    @JsonProperty("metadata")
    private ResponseMetadata metadata;
    
    @JsonProperty("timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        @JsonProperty("code")
        private String code;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("details")
        private String details;
        
        @JsonProperty("field_errors")
        private Map<String, List<String>> fieldErrors;
        
        @JsonProperty("trace_id")
        private String traceId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseMetadata {
        @JsonProperty("request_id")
        private String requestId;
        
        @JsonProperty("version")
        private String version;
        
        @JsonProperty("page")
        private Integer page;
        
        @JsonProperty("size")
        private Integer size;
        
        @JsonProperty("total_elements")
        private Long totalElements;
        
        @JsonProperty("total_pages")
        private Integer totalPages;
    }
    
    // Convenience factory methods
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message, String details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }
}
