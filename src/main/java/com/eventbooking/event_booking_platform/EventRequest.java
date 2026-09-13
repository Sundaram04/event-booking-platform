package com.eventbooking.event_booking_platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class EventRequest {
	
	@NotBlank(message = "Title must not be blank")
	@Size(max=120, message="Title must be under 120 characters")
	private String title;
	
	@Size(max=2000, message="Description must be under 2000 characters")
	private String description;
	
	@NotBlank(message="Date is required")
	private String date;
	
	@NotBlank(message="Location must not be blank")
	private String location;
	
	@PositiveOrZero(message="Price cannot be negative")
	private double price;
	
	@Positive(message="Capacity must be atleast 1")
	private int capacity;
	
	public EventRequest() {}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

	public String getLocation() {
		return location;
	}

	public double getPrice() {
		return price;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	
	

}
