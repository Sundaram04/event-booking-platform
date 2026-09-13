package com.eventbooking.event_booking_platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageImpl;
import java.util.ArrayList;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.eventbooking.event_booking_platform.dto.PageResponse;
import com.eventbooking.event_booking_platform.exception.EventNotEditableException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private EventService eventService;

	private Events sampleEvent;

	@BeforeEach
	void setUp() {
		sampleEvent = new Events();
		sampleEvent.setId(1L);
		sampleEvent.setTitle("Music Test");
		sampleEvent.setDescription("Live Bands");
		sampleEvent.setDate("2026-11-15");
		sampleEvent.setLocation("Mumbai");
		sampleEvent.setPrice(999.0);
		sampleEvent.setCapacity(100);

	}

	private Events createEvent(Long id, String title, Double price) {

		Events event = new Events();

		event.setId(id);
		event.setTitle(title);
		event.setDescription("Test Event");
		event.setDate("2026-12-01");
		event.setLocation("Bangalore");
		event.setPrice(price);
		event.setCapacity(100);

		return event;
	}

	@Test
	void getEventById_shouldReturnEvent_whenEventExists() {
		// ARRANGE
		when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

		// ACT
		EventResponse result = eventService.getEventById(1L);

		// ASSERT
		assertEquals("Music Test", result.getTitle());
		assertEquals("Mumbai", result.getLocation());
		assertEquals(100, result.getCapacity());
	}

	@Test
	void getEventById_shouldThrowResourceNotFoundException_whenEventDoesNotExists() {
		// ARRANGE
		when(eventRepository.findById(999L)).thenReturn(Optional.empty());

		// ACT+ASSERT
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> eventService.getEventById(999L));

		assertEquals("Event not found with id 999", exception.getMessage());
		verify(eventRepository, times(1)).findById(999L);

	}

	@Test
	void updateEvent_shouldThrowEventNotEditableException_whenEventDateHasPassed() {
		// ARRANGE
		GrantedAuthority adminAuthority = mock(GrantedAuthority.class);
		when(adminAuthority.getAuthority()).thenReturn("ROLE_ADMIN");
		doReturn(List.of(adminAuthority)).when(authentication).getAuthorities();

		Events pastEvent = new Events();
		pastEvent.setId(5L);
		pastEvent.setDate("2020-01-01");

		when(eventRepository.findById(5L)).thenReturn(Optional.of(pastEvent));

		EventRequest request = new EventRequest();
		request.setTitle("Update Title");
		request.setDate("2020-01-01");
		request.setLocation("Delhi");
		request.setPrice(100);
		request.setCapacity(50);

		// ACT + ASSERT
		assertThrows(EventNotEditableException.class, () -> eventService.updateEvent(5L, request, authentication));

		verify(eventRepository, never()).save(any(Events.class));
	}

	@Test
	void getAllEvents_returnsRequestedPageSize() {

		// ARRANGE
		List<Events> events = new ArrayList<>();

		for (long i = 1; i <= 10; i++) {
			Events event = new Events();
			event.setId(i);
			event.setTitle("Event " + i);
			event.setDescription("Test Event");
			event.setDate("2026-12-01");
			event.setLocation("Bangalore");
			event.setPrice(i * 100.0);
			event.setCapacity(100);

			events.add(event);
		}

		PageImpl<Events> page = new PageImpl<>(events, PageRequest.of(0, 10, Sort.by("id")), 25);

		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// ACT
		PageResponse<EventResponse> result = eventService.getAllEvents(null, null, null, null,null, null, 
				PageRequest.of(0, 10, Sort.by("id")));

		// ASSERT
		assertThat(result.getContent()).hasSize(10);
		assertThat(result.getTotalElements()).isEqualTo(25);
		assertThat(result.getTotalPages()).isEqualTo(3);
		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isFalse();
	}

	@Test
	void getAllEvents_sortsByPriceAscending() {

		// ARRANGE

		List<Events> events = List.of(createEvent(1L, "Event 1", 500.0), createEvent(2L, "Event 2", 100.0),
				createEvent(3L, "Event 3", 800.0), createEvent(4L, "Event 4", 200.0),
				createEvent(5L, "Event 5", 300.0));

		PageImpl<Events> page = new PageImpl<>(events, PageRequest.of(0, 25, Sort.by("price").ascending()), 5);

		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// ACT

		eventService.getAllEvents(null, null, null, null,null, null, PageRequest.of(0, 25, Sort.by("price").ascending()));

		// ASSERT

		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

		verify(eventRepository).findAll(any(Specification.class), pageableCaptor.capture());

		Pageable capturedPageable = pageableCaptor.getValue();

		assertThat(capturedPageable.getSort().getOrderFor("price").isAscending()).isTrue();
	}

	@Test
	void getAllEvents_filterByLocationCaseInsensitively() {

		// ARRANGE
		List<Events> events = List.of(createEvent(1L, "Event 1", 500.0), createEvent(2L, "Event 2", 200.0),
				createEvent(3L, "Event 3", 300.0));

		PageImpl<Events> page = new PageImpl<>(events, PageRequest.of(0, 10, Sort.by("id")), 3);

		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// ACT
		PageResponse<EventResponse> result = eventService.getAllEvents("bangalore", null, null, null,null, null,
				PageRequest.of(0, 10, Sort.by("id")));

		// ASSERT
		assertThat(result.getContent()).allMatch(e -> e.getLocation().equalsIgnoreCase("Bangalore"));
	}

	@Test
	void getAllEvents_combineLocationAndPriceFilter() {

		// ARRANGE
		Events event1 = createEvent(1L, "Event 1", 100.0);
		event1.setLocation("Delhi");

		Events event2 = createEvent(2L, "Event 2", 200.0);
		event2.setLocation("Delhi");

		Events event3 = createEvent(3L, "Event 3", 300.0);
		event3.setLocation("Delhi");

		List<Events> events = List.of(event1, event2, event3);

		PageImpl<Events> page = new PageImpl<>(events, PageRequest.of(0, 10, Sort.by("id")), 3);

		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// ACT
		PageResponse<EventResponse> result = eventService.getAllEvents("Delhi", null, null, 300.0,null, null,
				PageRequest.of(0, 10, Sort.by("id")));

		// ASSERT
		assertThat(result.getContent())
				.allMatch(e -> e.getLocation().equalsIgnoreCase("Delhi") && e.getPrice() <= 300.0);
	}
	
	@Test
	void getAllEvents_filtersByDateRange() {
		// ARRANGE
		Events event1 = createEvent(1L, "Event 1", 500.0);
		event1.setDate("2026-01-15");
		
		Events event2 = createEvent(2L, "Event 2", 700.0);
		event2.setDate("2026-03-20");
		
		Events event3 = createEvent(3L, "Event 3", 900.0);
		event3.setDate("2026-08-10");
		
		List<Events> events = List.of(event1, event2);
		
		PageImpl<Events> page = new PageImpl<>(
				events, PageRequest.of(0,  10, Sort.by("id")), 2
				);
		
		
//		act
		when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
		
		PageResponse<EventResponse> result = eventService.getAllEvents(null, null, null, null,   "2026-01-01",
                "2026-06-30",
                PageRequest.of(0, 10, Sort.by("id")));
		//assert
		
		assertThat(result.getContent()).allMatch(event -> 
		event.getDate().compareTo("2026-01-01") >= 0 &&  event.getDate().compareTo("2026-06-30") <= 0 );
		
		 assertThat(result.getContent()).hasSize(2);	
		
	}
}
