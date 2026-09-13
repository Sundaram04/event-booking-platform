package com.eventbooking.event_booking_platform.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eventbooking.event_booking_platform.EventResponse;
import com.eventbooking.event_booking_platform.EventService;
import com.eventbooking.event_booking_platform.config.SecurityConfig;
import com.eventbooking.event_booking_platform.security.JwtService;


@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
class EventControllerSecurityTest {

    @TestConfiguration
    static class MockMvcSecurityConfig {

        @Bean
        MockMvcBuilderCustomizer securityMockMvcCustomizer() {
            return builder -> builder.apply(springSecurity());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtService jwtService;
    
    private String validEventRequestJson() {

        return """
                {
                  "title": "Music Fest",
                  "description": "Live bands",
                  "date": "2026-11-15",
                  "location": "Mumbai",
                  "price": 999,
                  "capacity": 100
                }
                """;
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createEvent_shouldReturn403_whenRoleIsUser() throws Exception {

        mockMvc.perform(
                post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validEventRequestJson())
            )
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void createEvent_shouldReturn201_whenRoleIsOrganizer() throws Exception {

        when(eventService.createEvent(any(), any()))
                .thenReturn(new EventResponse());

        mockMvc.perform(
                post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validEventRequestJson())
            )
            .andExpect(status().isCreated());
    }

}
