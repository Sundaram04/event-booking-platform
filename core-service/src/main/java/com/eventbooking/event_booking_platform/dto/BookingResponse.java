package com.eventbooking.event_booking_platform.dto;

import java.time.LocalDateTime;

public class BookingResponse {
	
    private Long id;
    private Long eventId;
    private String userEmail;
    private int seatsBooked;
    private String status;
    private LocalDateTime bookedAt;
    
    public BookingResponse() {
    	
    }
    
    
	public BookingResponse(Long id, Long eventId, String userEmail, int seatsBooked, String status,
			LocalDateTime bookedAt) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.userEmail = userEmail;
		this.seatsBooked = seatsBooked;
		this.status = status;
		this.bookedAt = bookedAt;
	}
	public Long getId() {
		return id;
	}
	public Long getEventId() {
		return eventId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public int getSeatsBooked() {
		return seatsBooked;
	}
	public String getStatus() {
		return status;
	}
	public LocalDateTime getBookedAt() {
		return bookedAt;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public void setSeatsBooked(int seatsBooked) {
		this.seatsBooked = seatsBooked;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setBookedAt(LocalDateTime bookedAt) {
		this.bookedAt = bookedAt;
	}
    
    

}
