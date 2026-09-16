package com.eventbooking.event_booking_platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventBookingPlatformIntegrationTest {

	@TestConfiguration
	static class MockMvcSecurityConfig {

		@Bean
		MockMvcBuilderCustomizer securityMockMvcCustomizer() {
			return builder -> builder.apply(springSecurity());
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EventRepository eventRepository;
	

	@Test
	@WithMockUser(roles = "ORGANIZER")
	void createEvent_thenGetEventById_shouldReturnSameEvent() throws Exception {
		String requestJson = """
				{
				  "title": "Tech Conference",
				  "description": "Annual dev meetup",
				  "date": "2026-12-01",
				  "location": "Bengaluru",
				  "price": 0,
				  "capacity": 250
				}
				""";

		MvcResult result = mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Tech Conference")).andReturn();
		
		String responseJson = result.getResponse().getContentAsString();
		
	
		
		Number eventIdNumber = JsonPath.read(responseJson, "$.id");
		long eventId = eventIdNumber.longValue();
		
		mockMvc.perform(get("/api/v1/events/" + eventId))
		
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("Tech Conference"));
		
		long count = eventRepository.findAll().stream().filter(e -> e.getTitle().equals("Tech Conference"))
				.count();

		assertEquals(1, count);



	}

	@Test
	@WithMockUser(roles = "USER")
	void getAllEvents_keywordSearch_shouldReturnOnlyMatchingTitles() throws Exception {

		eventRepository.save(new Events(null, "Tech Conference", "Annual dev meetup", "2026-12-01", "Bengaluru", 0,
				"organizer@test.com", 250, 250));
		eventRepository.save(new Events(null, "Cooking Workshop", "Learn to cook", "2026-12-05", "Mumbai", 0,
				"organizer@test.com", 30, 30));

		mockMvc.perform(get("/api/v1/events").param("keyword", "conf"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$.content[*].title", everyItem(containsStringIgnoringCase("conf"))));
	}

}