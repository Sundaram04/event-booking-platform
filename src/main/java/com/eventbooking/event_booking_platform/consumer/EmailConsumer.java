package com.eventbooking.event_booking_platform.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eventbooking.event_booking_platform.dto.BookingCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmailConsumer {
	private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);
	
	@KafkaListener(
			topics = "booking-created",
			groupId = "email-service-group"
	)
	public void onBookingCreated(BookingCreatedEvent event) {
		log.info("Sending confirmation email for booking {}", event.getBookingId());
	}

}
