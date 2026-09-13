package com.eventbooking.event_booking_platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.UserResponse;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {
	private UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserResponse> me (Authentication authentication) {
		String email = authentication.getName();
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Authenticated User not found in DB"));
		
		UserResponse response = new UserResponse(user.getName(), user.getEmail());
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		System.out.println(">>> ADMIN METHOD REACHED");
		List<UserResponse> users = userRepository.findAll()
				.stream()
				.map(user -> new UserResponse(
						user.getName(), 
						user.getEmail()))
				.toList();
		return ResponseEntity.ok(users);
		
	}
	

}
