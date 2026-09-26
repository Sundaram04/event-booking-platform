package com.eventbooking.userservice.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//import com.eventbooking.event_booking_platform.model.User;

import com.eventbooking.userservice.model.User;


public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String email);
}
