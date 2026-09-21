package com.eventbooking.event_booking_platform.dto;

public class AuthResponse {
	private String token;
	private String name;
	private String email;
	
	
	public String getToken() {
		return token;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
