package com.eventbooking.event_booking_platform.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eventbooking.event_booking_platform.dto.UserSummaryResponse;
import com.eventbooking.event_booking_platform.exception.UserServiceUnavailableException;

@Component
public class UserServiceClient {
	private final RestTemplate restTemplate;
	private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
	
	@Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

	public UserServiceClient(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}
	
	public UserSummaryResponse getUser(Long userId) {
		String url = userServiceUrl + "/internal/users/" + userId;
		
		try {
			return restTemplate.getForObject(url, UserSummaryResponse.class);
			
			
		} catch( RestClientException ex) {
			log.error("User Service call failed for userId={}", userId, ex);
			throw new UserServiceUnavailableException("Could not reach User Service for user" + userId); 
			
		}
		
		
	}
	
	

}
