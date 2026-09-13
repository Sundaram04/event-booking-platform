package com.eventbooking.event_booking_platform.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.AuthResponse;
import com.eventbooking.event_booking_platform.dto.LoginRequest;
import com.eventbooking.event_booking_platform.dto.RegisterRequest;
import com.eventbooking.event_booking_platform.model.Role;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.UserRepository;
import com.eventbooking.event_booking_platform.security.JwtService;

@Service
public class AuthService {

	private UserRepository userRepository; // final
	private PasswordEncoder passwordEncoder; // final

	private JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

		userRepository.save(user);

		AuthResponse response = new AuthResponse();
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		// token

		return response;
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		String token = jwtService.generateToken(user.getEmail(), user.getRole());

		AuthResponse response = new AuthResponse();
		response.setToken(token);
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		return response;

	}

}
