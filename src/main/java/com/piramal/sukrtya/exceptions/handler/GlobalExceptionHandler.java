package com.piramal.sukrtya.exceptions.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    // Handle all exceptions globally
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, HttpServletRequest request) {

        // Log the error with request details
        logError("Unhandled exception occurred", ex, request);

        // Return a standardized error response
        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                "An unexpected error occurred. Please try again later.",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private void logError(String message, Exception ex, HttpServletRequest request) {
        String requestInfo = String.format(
                "Request: method=%s, URI=%s, Params=%s, IP=%s",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr()
        );
        logger.error("{} - {} - Exception: {}", message, requestInfo, ex.getMessage(), ex);
    }
}


