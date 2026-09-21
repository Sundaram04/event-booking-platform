package com.eventbooking.event_booking_platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.BookingRequest;
import com.eventbooking.event_booking_platform.dto.BookingResponse;
import com.eventbooking.event_booking_platform.entity.IdempotencyKey;
import com.eventbooking.event_booking_platform.exception.IdempotencyConflictException;
import com.eventbooking.event_booking_platform.exception.RequestInProgressException;
import com.eventbooking.event_booking_platform.service.BookingService;
import com.eventbooking.event_booking_platform.service.IdempotencyService;

import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
	private final BookingService bookingService;
	private final IdempotencyService idempotencyService;
	private final ObjectMapper objectMapper;

	public BookingController(BookingService bookingService, IdempotencyService idempotencyService,
			ObjectMapper objectMapper) {

		this.bookingService = bookingService;
		this.idempotencyService = idempotencyService;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/{eventId}")
	public ResponseEntity<?> book(@PathVariable Long eventId, @RequestHeader("Idempotency-Key") String idempotencyKey,
			@Valid @RequestBody BookingRequest request, Authentication authentication) {
		String userEmail = authentication.getName();
		String requestJson = objectMapper.writeValueAsString(request);
		String requestHash = DigestUtils.sha256Hex(eventId + ":" + requestJson);

		Optional<IdempotencyKey> existing = idempotencyService.claim(idempotencyKey, requestHash);

		if (existing.isPresent()) {
			IdempotencyKey found = existing.get();

			if (!found.getRequestHash().equals(requestHash)) {
				throw new IdempotencyConflictException("Idempotency-Key reused with a different request body");
			}
			if (found.getResponseBody() == null) {
				throw new RequestInProgressException("This request is already being processed");
			}

			return ResponseEntity.status(found.getResponseStatus()).body(found.getResponseBody());
		}

		BookingResponse response;
		try {
			response = bookingService.createBooking(eventId, userEmail, request.getSeats());
		} catch (RuntimeException ex) {
			idempotencyService.release(idempotencyKey);
			throw ex;
		}

		String responseJson = objectMapper.writeValueAsString(response);

		idempotencyService.complete(idempotencyKey, 201, responseJson);

		return ResponseEntity.status(201).body(response);
	}

}
