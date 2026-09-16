package com.eventbooking.event_booking_platform;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private final String validLoginJson = """
            {
                "email": "test@gmail.com",
                "password": "wrongPassword"
            }
            """;

    @Test
    void loginEndpoint_shouldReturn429_onSixthRapidAttempt() throws Exception {

        for (int i = 0; i < 5; i++) {

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginJson)
            )
            .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validLoginJson)
        )
        .andExpect(status().is(429))
        .andExpect(header().exists("Retry-After"));
    }
}