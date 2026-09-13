package com.eventbooking.event_booking_platform;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.PageResponse;
import com.eventbooking.event_booking_platform.exception.EventNotEditableException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Events;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.EventSpecification;

@Service
public class EventService {
	public final Logger log = LoggerFactory.getLogger(EventService.class);
	public final EventRepository eventRepository; // final

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	private EventResponse toResponse(Events event) {
		return new EventResponse(event.getId(), event.getTitle(), event.getDescription(), event.getDate(),
				event.getLocation(), event.getPrice(), event.getCapacity());
	}

	public PageResponse<EventResponse> getAllEvents(String location, String keyword, Double minPrice, Double maxPrice,
			String dateFrom, String dateTo, Pageable pageable) {

		Specification<Events> spec = Specification
				.where(EventSpecification.hasLocation(location).and(EventSpecification.titleContains(keyword)))
				.and(EventSpecification.priceBetween(minPrice, maxPrice))
				.and(EventSpecification.dateBetween(dateFrom, dateTo));

		Page<Events> events = eventRepository.findAll(spec, pageable);

		log.info("Fetched {} of {} events (page={}, size={})", events.getNumberOfElements(), events.getTotalElements(),
				pageable.getPageNumber(), pageable.getPageSize());

		Page<EventResponse> mapped = events.map(this::toResponse);
		return PageResponse.from(mapped);

	}

	public EventResponse getEventById(Long id) {
		log.debug("Fetching event with id={}", id);

		Events event = eventRepository.findById(id).orElseThrow(() -> {
			log.warn("Event not found for id={}", id);
			return new ResourceNotFoundException("Event not found with id " + id);

		});
		log.info("Event fetched successfully: id={}, title={}", event.getId(), event.getTitle());

		return toResponse(event);
	}

	public EventResponse createEvent(EventRequest request, Authentication authentication) {
		Events event = new Events();

		event.setTitle(request.getTitle());
		event.setDescription(request.getDescription());
		event.setDate(request.getDate());
		event.setLocation(request.getLocation());
		event.setPrice(request.getPrice());
		event.setOrganizerEmail(authentication.getName());
		event.setCapacity(request.getCapacity());
		event.setAvailableSeats(request.getCapacity());

		Events saved = eventRepository.save(event);
		return toResponse(saved);

	}

	public EventResponse updateEvent(Long id, EventRequest request, Authentication authentication) {
		Events event = eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
//		Events event = existing.get();
//		
		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin && !authentication.getName().equals(event.getOrganizerEmail())) {

			throw new AccessDeniedException("You can edit your event only.");
		}

		LocalDate eventDate = LocalDate.parse(event.getDate());

		if (eventDate.isBefore(LocalDate.now())) {

			throw new EventNotEditableException("Cannot update event id " + id + " — it has already started");
//			new RuntimeException("TESTING UNEXPECTED EXCEPTION")
		}

		event.setTitle(request.getTitle());
		event.setDescription(request.getDescription());
		event.setDate(request.getDate());
		event.setLocation(request.getLocation());
		event.setPrice(request.getPrice());
		event.setCapacity(request.getCapacity());
		Events updated = eventRepository.save(event);
		return toResponse(updated);
	}

	public void deleteEvent(Long id) {
		if (!eventRepository.existsById(id)) {
			throw new ResourceNotFoundException("Event not found with id " + id);
		}

		eventRepository.deleteById(id);
	}

}
