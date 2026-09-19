package com.eventbooking.event_booking_platform.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eventbooking.event_booking_platform.dto.BookingCreatedEvent;

@Component
public class NotificationConsumer {

    @KafkaListener(
            topics = "booking-created",
            groupId = "notification-service-group"
    )
    public void onBookingCreated(BookingCreatedEvent event) {

        // Existing notification logic
        // notificationService.sendPush(
        //         event.getUserEmail(),
        //         event.getBookingId()
        // );
    }
}