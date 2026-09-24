package com.eventbooking.event_booking_platform.entity;

import java.time.LocalDateTime;

import com.eventbooking.event_booking_platform.model.Events;

import jakarta.persistence.*;

@Entity
@Table(name="booking")
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="event_id", nullable = false)
	private Events event;
	
	@Column(name="user_email", nullable = false)
	private String userEmail;
	
	@Column(name="seats_booked", nullable = false)
	private int seatsBooked;
	
	@Column(nullable= false)
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	
	@Column(name="booked_at", nullable= false)
	private LocalDateTime bookedAt;

	public Booking() {}
	public Booking(Long id, Events event, String userEmail, int seatsBooked, BookingStatus status,
			LocalDateTime bookedAt) {
		super();
		this.id = id;
		this.event = event;
		this.userEmail = userEmail;
		this.seatsBooked = seatsBooked;
		this.status = status;
		this.bookedAt = bookedAt;
	}

	public Long getId() {
		return id;
	}

	public Events getEvent() {
		return event;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public int getSeatsBooked() {
		return seatsBooked;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public LocalDateTime getBookedAt() {
		return bookedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setEvent(Events event) {
		this.event = event;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setSeatsBooked(int seatsBooked) {
		this.seatsBooked = seatsBooked;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public void setBookedAt(LocalDateTime bookedAt) {
		this.bookedAt = bookedAt;
	}
	
	
	
	

}
