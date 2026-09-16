package com.eventbooking.event_booking_platform.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.EventRequest;
import com.eventbooking.event_booking_platform.EventResponse;
import com.eventbooking.event_booking_platform.EventService;
import com.eventbooking.event_booking_platform.dto.PageResponse;
import com.eventbooking.event_booking_platform.model.Events;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "price", "date", "location",
			"capacity");

	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public PageResponse<EventResponse> getAllEvents(@RequestParam(required = false) String location,
			@RequestParam(required = false) String keyword, @RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir,
			@RequestParam(required = false) String dateFrom,
			@RequestParam(required = false) String dateTo
			) {
		
		if(!ALLOWED_SORT_FIELDS.contains(sortBy)) {
			throw new IllegalArgumentException("Invalid sort field: " + sortBy);
		}
		
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		return eventService.getAllEvents(location, keyword, minPrice, maxPrice, dateFrom, dateTo, pageable);
		
		
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
		return ResponseEntity.ok(eventService.getEventById(id));
	}

	@PostMapping
	public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request,
			Authentication authentication) {
		EventResponse created = eventService.createEvent(request, authentication);
		return new ResponseEntity<>(created, HttpStatus.CREATED);

	}

	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request,
			Authentication authentication) {
		EventResponse updated = eventService.updateEvent(id, request, authentication);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
		eventService.deleteEvent(id);
		return ResponseEntity.noContent().build();
	}

}
