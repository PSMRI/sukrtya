package com.sukrtya.siwan.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Last-resort exception translator. Without this, uncaught exceptions (NPE, DB constraint
 * violations, synthetic-class loading errors, JPA errors, ...) come out of Spring's default
 * {@code ErrorAttributes} with cryptic content (e.g. just the internal class name of a
 * {@link NoClassDefFoundError}). We catch everything here and emit a stable, debuggable JSON shape
 * — {@code {timestamp, status, error, message, path}} — that mirrors what we already do for
 * {@link ResponseStatusException}.
 *
 * <p>Stack traces are written to the application log; only the exception's {@code message} (and
 * its simple class name) is exposed to clients so we don't leak server internals.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatus(
			ResponseStatusException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		// 5xx are real bugs; 4xx are expected client mistakes. Log accordingly.
		if (status.is5xxServerError()) {
			log.error("Unhandled {} on {}", status, request.getRequestURI(), ex);
		}
		else {
			log.debug("Client error {} on {}: {}", status, request.getRequestURI(), ex.getReason());
		}
		return body(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), request);
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<Map<String, Object>> handleBadInput(RuntimeException ex, HttpServletRequest request) {
		log.warn("Bad input on {}: {}", request.getRequestURI(), ex.getMessage());
		return body(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleFallback(
			Exception ex, HttpServletRequest request, WebRequest webRequest) {
		// Log the full stack trace once, with the request URI for correlation.
		log.error("Internal error on {} {}", request.getMethod(), request.getRequestURI(), ex);
		String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
				? ex.getClass().getSimpleName() + ": " + ex.getMessage()
				: ex.getClass().getSimpleName() + " (see server logs for stack trace)";
		return body(HttpStatus.INTERNAL_SERVER_ERROR, detail, request);
	}

	private static ResponseEntity<Map<String, Object>> body(HttpStatus status, String message, HttpServletRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", request.getRequestURI());
		return ResponseEntity.status(status).body(body);
	}
}
