package com.eventbooking.event_booking_platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

// Data Model
@Entity
public class Events {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;
	private String date;
	private String location;
	private double price;
	private String organizerEmail;
	private int capacity;

	@Column(name = "available_seats", nullable = false)
	private int availableSeats;
//
//	@Version
//	private Long version;

	// Becoz Jackson uses this no-args constructor to create JSON
	public Events() {
	}

	public Events(Long id, String title, String description, String date, String location, double price,
			String organizerEmail, int capacity, int availableSeats) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.date = date;
		this.location = location;
		this.price = price;
		this.organizerEmail = organizerEmail;
		this.capacity = capacity;
		this.availableSeats = availableSeats;
	}

	public Long getId() {
		return id;
	}

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

	public void setId(Long id) {
		this.id = id;
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

	public String getOrganizerEmail() {
		return organizerEmail;
	}

	public void setOrganizerEmail(String organizerEmail) {
		this.organizerEmail = organizerEmail;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}



	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}



}
