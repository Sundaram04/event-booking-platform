package com.eventbooking.event_booking_platform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.service.BookingService;

@SpringBootTest
public class BookingServiceConcurrencyTest {

	@Autowired
	private BookingService bookingService;
	@Autowired
	private EventRepository eventRepository;

	@Test
	void twoUsersBookingLastSeat_onlyOneShouldSucceed() throws InterruptedException {
		Events event = eventRepository
				.save(new Events(null, "Concert", "desc", "2026-12-01", "Bangalore", 500.0, null, 1, 1));

		int numberOfThreads = 2;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
		CountDownLatch startLatch = new CountDownLatch(1);

		List<Boolean> results = new CopyOnWriteArrayList<>();

		Runnable bookingTask = () -> {
			readyLatch.countDown();
			try {
				startLatch.await();
				bookingService.createBooking(event.getId(), "user@test.com", 1);
				results.add(true);
			} catch (InsufficientSeatsException e) {
				results.add(false);
			} catch (Exception e) {
				results.add(false);
			}
		};

		executor.submit(bookingTask);
		executor.submit(bookingTask);

		readyLatch.await();
		startLatch.countDown();
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		long successCount = results.stream().filter(r -> r).count();
		assertThat(successCount).isEqualTo(1);
		Events finalEvent = eventRepository.findById(event.getId()).orElseThrow();
		assertThat(finalEvent.getAvailableSeats()).isEqualTo(0);

	}

}
