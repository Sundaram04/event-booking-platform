package com.eventbooking.event_booking_platform.config;



import java.time.Duration;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(3))
                .build();
		
	}

}
