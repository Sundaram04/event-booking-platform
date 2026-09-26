package com.eventbooking.event_booking_platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event_booking_platform.model.User;

public interface UserRepository extends JpaRepository <User, Long>{
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String mail);
	

}
