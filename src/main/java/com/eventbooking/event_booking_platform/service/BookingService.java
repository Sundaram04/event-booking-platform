package com.eventbooking.event_booking_platform.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event_booking_platform.dto.BookingResponse;
import com.eventbooking.event_booking_platform.entity.Booking;
import com.eventbooking.event_booking_platform.entity.BookingStatus;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;

import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;

import java.time.LocalDateTime;

import org.slf4j.*;

@Service
public class BookingService {
	private static final Logger log = LoggerFactory.getLogger(BookingService.class);

	private final BookingRepository bookingRepository;
	private final EventRepository eventRepository;

	public BookingService(BookingRepository bookingRepository, EventRepository eventRepository) {
		super();
		this.bookingRepository = bookingRepository;
		this.eventRepository = eventRepository;
	}
	
	private BookingResponse toResponse(Booking booking) {
		return new BookingResponse(booking.getId(), booking.getEvent().getId(), booking.getUserEmail(), booking.getSeatsBooked(), booking.getStatus().toString(), booking.getBookedAt());
	}
	

	@Transactional
	public BookingResponse createBooking(Long eventId, String userEmail, int seatsRequested) {
		Events event = eventRepository.findByIdForUpdate(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not Found: " + eventId));

		if (event.getAvailableSeats() < seatsRequested) {
			log.warn("Booking Rejected - eventId={}, requested={}, available={}", eventId, seatsRequested,
					event.getAvailableSeats());

			throw new InsufficientSeatsException("Only " + event.getAvailableSeats() + "seats left");
		}

		event.setAvailableSeats(event.getAvailableSeats() - seatsRequested);
		eventRepository.save(event);

		Booking booking = new Booking();
		booking.setEvent(event);
		booking.setUserEmail(userEmail);
		booking.setSeatsBooked(seatsRequested);
		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setBookedAt(LocalDateTime.now());

		Booking saved = bookingRepository.save(booking);
		log.info("Booking Confirmed - bookingId={}, eventId={}, user={}, seats={}", saved.getId(), eventId, userEmail,
				seatsRequested);
		return toResponse(saved);

	}

}
