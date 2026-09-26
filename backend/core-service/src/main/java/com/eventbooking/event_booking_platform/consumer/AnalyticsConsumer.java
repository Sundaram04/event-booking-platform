package com.eventbooking.event_booking_platform.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eventbooking.event_booking_platform.dto.BookingCreatedEvent;

@Component
public class AnalyticsConsumer {

    @KafkaListener(
            topics = "booking-created",
            groupId = "analytics-service-group"
    )
    public void onBookingCreated(BookingCreatedEvent event) {

        // Existing analytics logic
        // analyticsService.recordBooking(
        //         event.getEventId(),
        //         event.getSeatsBooked()
        // );
    }
}