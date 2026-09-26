package com.eventbooking.event_booking_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.service.BookingService;
import com.eventbooking.event_booking_platform.client.UserServiceClient;
import com.eventbooking.event_booking_platform.dto.BookingCreatedEvent;
import com.eventbooking.event_booking_platform.dto.UserSummaryResponse;
import com.eventbooking.event_booking_platform.entity.Booking;
import com.eventbooking.event_booking_platform.entity.BookingStatus;
import com.eventbooking.event_booking_platform.model.Events;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private UserServiceClient userServiceClient;

	@Mock
	private KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

	@InjectMocks
	private BookingService bookingService;
	
	@Test
	void createBooking_shouldPublishBookingCreatedEvent() {
		
		// test data
		 Long eventId = 10L;
	        Long userId = 5L;
	        String userEmail = "test@gmail.com";
	        int seatsRequested = 2;

	        Long bookingId = 101L;

	        LocalDateTime bookedAt = LocalDateTime.now();
	        
	        
	        Events event = new Events();
	        
	        event.setId(eventId);
	        event.setAvailableSeats(10);
	        
	        when(eventRepository.findByIdForUpdate(eventId)).thenReturn(Optional.of(event));

	        when(userServiceClient.getUser(userId)).thenReturn(new UserSummaryResponse(userId, userEmail, true));

	        Booking savedBooking = new Booking();
	        
	        savedBooking.setId(bookingId);
	        savedBooking.setEvent(event);
	        savedBooking.setUserEmail(userEmail);
	        savedBooking.setSeatsBooked(seatsRequested);
	        savedBooking.setStatus(BookingStatus.CONFIRMED);
	        savedBooking.setBookedAt(bookedAt);
	        
	        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
	        
	        bookingService.createBooking(eventId, userId, userEmail, seatsRequested);
	        
	        ArgumentCaptor<BookingCreatedEvent> captor =
	                ArgumentCaptor.forClass(BookingCreatedEvent.class);
	        
	        verify(kafkaTemplate).send(
	                eq("booking-created"),
	                eq(bookingId.toString()),
	                captor.capture()
	        );
	        
	        BookingCreatedEvent capturedEvent =
	                captor.getValue();

	        assertThat(capturedEvent.getBookingId())
            .isEqualTo(bookingId);
	        
	        assertThat(capturedEvent.getEventId())
            .isEqualTo(eventId);
	        
	        assertThat(capturedEvent.getUserEmail())
            .isEqualTo(userEmail);
	        
	        assertThat(capturedEvent.getSeatsBooked())
            .isEqualTo(seatsRequested);

	        assertThat(capturedEvent.getBookedAt())
            .isEqualTo(bookedAt);
	        
	        
		
	}
	
	
	

	
	

}
