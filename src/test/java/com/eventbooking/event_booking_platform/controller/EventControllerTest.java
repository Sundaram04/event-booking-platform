package com.eventbooking.event_booking_platform.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventbooking.event_booking_platform.EventResponse;
import com.eventbooking.event_booking_platform.EventService;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private EventService eventService;
	@MockitoBean
	private JwtService jwtService;
	
	@Test
	void getEventByid_shouldReturn200_whenEventExists() throws Exception {
		EventResponse response = new EventResponse();
		
		response.setId(1L);
		response.setTitle("Music Fest");
		response.setLocation("Mumbai");
		when(eventService.getEventById(1L)).thenReturn(response);
		
		mockMvc.perform(get("/events/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("Music Fest"))
		.andExpect(jsonPath("$.location").value("Mumbai"));	
	}
	
	@Test
	void createEvent_shouldReturn400_whenTitleIsBlank() throws Exception {
		String invalidRequestJson = """ 
				{
				"title" : "",
				"description" : "Live bands",
				"date" : "2026-11-15",
				"location":"Mumbai",
				"price":999,
				"capacity":100
				}
				"""; 
		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidRequestJson))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.fieldErrors.title").exists());
	}
	
	@Test
	void getEventById_shouldReturn404_whenEventDoesNotExist() throws Exception {
		when(eventService.getEventById(999L)).thenThrow(new ResourceNotFoundException("Event not found with id 999"));
		
		mockMvc.perform(get("/events/999"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Event not found with id 999"));
	}
}
