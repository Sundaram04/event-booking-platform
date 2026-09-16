package com.eventbooking.event_booking_platform.exception;

public class RequestInProgressException extends RuntimeException {

    public RequestInProgressException(String message) {
        super(message);
    }
}
