package com.eventbooking.event_booking_platform.dto;

public class UserSummaryResponse {
	private Long id;
	private String email;
	private boolean active;
	public UserSummaryResponse(Long id, String email, boolean active) {
		super();
		this.id = id;
		this.email = email;
		this.active = active;
	}
	public Long getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public boolean isActive() {
		return active;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
