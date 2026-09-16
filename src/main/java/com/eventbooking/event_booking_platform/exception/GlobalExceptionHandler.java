package com.eventbooking.event_booking_platform.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.eventbooking.event_booking_platform.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> fieldErrors = new HashMap<>();

		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}

		ErrorResponse error = new ErrorResponse(Instant.now().toString(), 400, "Validation Failed",
				"Request contains invalid fields", request.getRequestURI(), fieldErrors);
		return ResponseEntity.badRequest().body(error);

	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(Instant.now().toString(), 404, "Not Found", ex.getMessage(),
				request.getRequestURI(), null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

	}

	@ExceptionHandler(EventNotEditableException.class)
	public ResponseEntity<ErrorResponse> handleBussinessRule(EventNotEditableException ex, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(Instant.now().toString(), 409, "Conflict", ex.getMessage(),
				request.getRequestURI(), null);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
			HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(Instant.now().toString(), 400, "Bad Request", ex.getMessage(),
				request.getRequestURI(), null);
		return ResponseEntity.badRequest().body(error);

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error at {}", request.getRequestURI(), ex);

		ErrorResponse error = new ErrorResponse(Instant.now().toString(), 500, "Internal Server Error",
				"Something went wrong. Please try again later.", request.getRequestURI(), null);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

	}

	@ExceptionHandler(IdempotencyConflictException.class)
	public ResponseEntity<ErrorResponse> handleIdempotencyConflict(IdempotencyConflictException ex,
			HttpServletRequest request) {

		ErrorResponse error = new ErrorResponse(java.time.Instant.now().toString(), HttpStatus.CONFLICT.value(),
				"Conflict", ex.getMessage(), request.getRequestURI(), null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(RequestInProgressException.class)
	public ResponseEntity<ErrorResponse> handleRequestInProgress(RequestInProgressException ex,
			HttpServletRequest request) {

		ErrorResponse error = new ErrorResponse(java.time.Instant.now().toString(), HttpStatus.CONFLICT.value(),
				"Conflict", ex.getMessage(), request.getRequestURI(), null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {

		ErrorResponse error = new ErrorResponse(java.time.Instant.now().toString(), HttpStatus.UNAUTHORIZED.value(),
				"Unauthorized", ex.getMessage(), request.getRequestURI(), null);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(InsufficientSeatsException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientSeats(InsufficientSeatsException ex,
			HttpServletRequest request) {

		ErrorResponse error = new ErrorResponse(Instant.now().toString(), HttpStatus.CONFLICT.value(), "Conflict",
				ex.getMessage(), request.getRequestURI(), null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
			HttpServletRequest request) {

		ErrorResponse error = new ErrorResponse(Instant.now().toString(), HttpStatus.NOT_FOUND.value(), "Not Found",
				"No endpoint found for " + request.getMethod() + " " + request.getRequestURI(),
				request.getRequestURI(), null);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
