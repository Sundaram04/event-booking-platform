package com.eventbooking.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.userservice.dto.UserSummaryResponse;
import com.eventbooking.userservice.model.User;
import com.eventbooking.userservice.repository.UserRepository;

@RestController
@RequestMapping("/internal/users")
public class UserController {
	
	public UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
	@GetMapping("/{id}")
	public UserSummaryResponse getUser(@PathVariable Long id) {
		User user = userRepository.findById(id).orElseThrow();
		
		return new UserSummaryResponse(user.getId(), user.getEmail(), user.isActive());
		
		
	}
	

}
