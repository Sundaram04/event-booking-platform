package com.eventbooking.event_booking_platform.dto;

public class UserResponse {
	private String name;
	private String email;
	public UserResponse(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	

}

