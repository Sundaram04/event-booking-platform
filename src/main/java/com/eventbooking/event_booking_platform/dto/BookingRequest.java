package com.eventbooking.event_booking_platform.dto;

import jakarta.validation.constraints.Positive;

public class BookingRequest {
	@Positive(message="Seats requested must be at least 1")
	private int seats;

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}
	
	

}
