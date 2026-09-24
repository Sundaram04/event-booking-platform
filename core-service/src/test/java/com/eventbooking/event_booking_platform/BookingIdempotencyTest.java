package com.eventbooking.event_booking_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.IdempotencyKeyRepository;

/**
 * Uses the real Postgres dev database (no Testcontainers/H2 configured for this project).
 * Each test creates its own uniquely-named Event and Idempotency-Key and cleans them up in
 * {@link #cleanup()}, and assertions are scoped to the specific event/key created by that test
 * rather than global table counts, so tests remain reliable even if the table already has rows
 * from earlier runs.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BookingIdempotencyTest {

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
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;

    private final String bookingJson = """
            {
                "seats": 1
            }
            """;

    private final List<Long> createdEventIds = new ArrayList<>();
    private final List<String> usedIdempotencyKeys = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (Long eventId : createdEventIds) {
            bookingRepository.deleteAll(bookingRepository.findByEventId(eventId));
            eventRepository.deleteById(eventId);
        }
        for (String key : usedIdempotencyKeys) {
            idempotencyKeyRepository.deleteById(key);
        }
        createdEventIds.clear();
        usedIdempotencyKeys.clear();
    }

    private Events createTestEvent(String title, int capacity, int availableSeats) {
        Events event = new Events(null, title, "Test Description", "2026-11-15", "Chennai", 500.0, "test@gmail.com",
                capacity, availableSeats);
        event = eventRepository.save(event);
        createdEventIds.add(event.getId());
        return event;
    }

    @Test
    @WithMockUser(roles = "USER")
    void sameIdempotencyKey_shouldReplayOriginalJsonResponse() throws Exception {

        String key = "test-key-replay-" + System.nanoTime();
        usedIdempotencyKeys.add(key);

        Events event = createTestEvent("Test Event", 100, 100);
        Long eventId = event.getId();

        MvcResult first = mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content(bookingJson)).andExpect(status().isCreated())
                .andReturn();

        MvcResult second = mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content(bookingJson)).andExpect(status().isCreated())
                .andReturn();

        String firstBody = first.getResponse().getContentAsString();
        String secondBody = second.getResponse().getContentAsString();

        // Guards against the historical bug where the stored/replayed response was
        // Object.toString() (e.g. "BookingResponse@1a2b3c4d") instead of real JSON.
        assertThat(secondBody).isEqualTo(firstBody);
        assertThat(secondBody).startsWith("{");
        assertThat(secondBody).contains("\"seatsBooked\"");

        assertThat(bookingRepository.findByEventId(eventId)).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void sameIdempotencyKey_differentBody_shouldReturn409() throws Exception {

        String key = "test-key-conflict-" + System.nanoTime();
        usedIdempotencyKeys.add(key);

        Events event = createTestEvent("Conflict Event", 100, 100);
        Long eventId = event.getId();

        mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content("{\"seats\": 1}")).andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content("{\"seats\": 2}")).andExpect(status().isConflict());

        assertThat(bookingRepository.findByEventId(eventId)).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void soldOutBooking_shouldReturn409AndNotLeaveKeyPermanentlyStuck() throws Exception {

        String key = "test-key-soldout-" + System.nanoTime();
        usedIdempotencyKeys.add(key);

        Events event = createTestEvent("Sold Out Event", 1, 0);
        Long eventId = event.getId();

        mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content(bookingJson)).andExpect(status().isConflict());

        // The failed attempt must release its claim, not leave it stuck as "in progress" forever.
        assertThat(idempotencyKeyRepository.findById(key)).isEmpty();

        // A retry with the same key must be processed fresh (same business conflict again),
        // not blocked behind a permanent RequestInProgressException.
        mockMvc.perform(post("/api/v1/bookings/" + eventId).header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON).content(bookingJson)).andExpect(status().isConflict());

        assertThat(bookingRepository.findByEventId(eventId)).isEmpty();
    }

    @Test
    void concurrentSameIdempotencyKey_shouldCreateExactlyOneBooking() throws Exception {

        String key = "test-key-concurrent-" + System.nanoTime();
        usedIdempotencyKeys.add(key);

        Events event = createTestEvent("Concurrent Event", 10, 10);
        Long eventId = event.getId();

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Integer> statuses = Collections.synchronizedList(new ArrayList<>());

        Runnable task = () -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                MvcResult result = mockMvc
                        .perform(post("/api/v1/bookings/" + eventId).with(user("concurrent-user").roles("USER"))
                                .header("Idempotency-Key", key).contentType(MediaType.APPLICATION_JSON)
                                .content(bookingJson))
                        .andReturn();
                statuses.add(result.getResponse().getStatus());
            } catch (Exception e) {
                statuses.add(-1);
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }

        readyLatch.await();
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Every racer either creates/replays successfully, or observes the claim still in
        // progress (409) — never a duplicate booking, never a 500.
        assertThat(statuses).allMatch(s -> s == 201 || s == 409);
        assertThat(bookingRepository.findByEventId(eventId)).hasSize(1);
    }
}
