package com.eventbooking.event_booking_platform.exception;

public class InsufficientSeatsException extends RuntimeException {
	public InsufficientSeatsException(String message) {
		super(message);
	}

}
