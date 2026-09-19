package com.eventbooking.event_booking_platform.dto;

import java.time.LocalDateTime;

public class BookingCreatedEvent {
	private Long bookingId;
	private Long eventId;
	private String userEmail;
	private Integer seatsBooked;
	private LocalDateTime bookedAt;
	public BookingCreatedEvent(Long bookingId, Long eventId, String userEmail, Integer seatsBooked,
			LocalDateTime bookedAt) {
		super();
		this.bookingId = bookingId;
		this.eventId = eventId;
		this.userEmail = userEmail;
		this.seatsBooked = seatsBooked;
		this.bookedAt = bookedAt;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public Long getEventId() {
		return eventId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public Integer getSeatsBooked() {
		return seatsBooked;
	}
	public LocalDateTime getBookedAt() {
		return bookedAt;
	}
	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public void setSeatsBooked(Integer seatsBooked) {
		this.seatsBooked = seatsBooked;
	}
	public void setBookedAt(LocalDateTime bookedAt) {
		this.bookedAt = bookedAt;
	}
	
	

}
