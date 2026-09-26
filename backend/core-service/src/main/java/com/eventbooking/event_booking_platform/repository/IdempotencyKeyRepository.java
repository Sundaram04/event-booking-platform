package com.eventbooking.event_booking_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event_booking_platform.entity.IdempotencyKey;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String>{

}
