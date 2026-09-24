package com.eventbooking.event_booking_platform;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ActuatorSecurityTest {

    @TestConfiguration
    static class MockMvcSecurityConfig {
        @Bean
        MockMvcBuilderCustomizer securityMockMvcCustomizer() {
            return builder -> builder.apply(springSecurity());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_shouldBeAccessibleWithoutAuth() throws Exception {

        mockMvc.perform(
                get("/actuator/health")
        )
        .andExpect(status().isOk());
    }

    @Test
    void metrics_shouldRequireAdminRole() throws Exception {

        mockMvc.perform(
                get("/actuator/metrics")
        )
        .andExpect(status().isUnauthorized());
    }
    
    
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void metrics_shouldBeAccessibleToAdmin() throws Exception {

        mockMvc.perform(
                get("/actuator/metrics")
        )
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void metrics_shouldBeForbiddenToUser() throws Exception {

        mockMvc.perform(
                get("/actuator/metrics")
        )
        .andExpect(status().isForbidden());
    }
}