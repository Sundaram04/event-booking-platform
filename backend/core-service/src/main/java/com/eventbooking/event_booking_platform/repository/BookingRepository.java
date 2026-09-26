package com.eventbooking.event_booking_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event_booking_platform.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{
	List<Booking> findUserByUserEmail(String userEmail);
	List<Booking> findByEventId(Long eventId);

}
