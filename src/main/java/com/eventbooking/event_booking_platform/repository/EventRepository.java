package com.eventbooking.event_booking_platform.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eventbooking.event_booking_platform.model.Events;

import jakarta.persistence.LockModeType;

@Repository
public interface EventRepository extends JpaRepository<Events, Long>, JpaSpecificationExecutor<Events> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT e FROM Events e WHERE e.id = :id")
	Optional<Events> findByIdForUpdate(Long id);
}
