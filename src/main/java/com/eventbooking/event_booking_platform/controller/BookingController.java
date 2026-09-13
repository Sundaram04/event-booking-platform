package com.eventbooking.event_booking_platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.BookingRequest;
import com.eventbooking.event_booking_platform.dto.BookingResponse;
import com.eventbooking.event_booking_platform.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookings")
public class BookingController {
	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/{eventId}")
	public ResponseEntity<BookingResponse> book(@PathVariable Long eventId, @Valid @RequestBody BookingRequest request,
			Authentication authentication) {
		String userEmail = authentication.getName();

		BookingResponse response = bookingService.createBooking(eventId, userEmail, request.getSeats());
		return ResponseEntity.ok(response);
	}

}
