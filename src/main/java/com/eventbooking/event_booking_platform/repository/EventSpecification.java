package com.eventbooking.event_booking_platform.repository;

import org.springframework.data.jpa.domain.Specification;

import com.eventbooking.event_booking_platform.model.Events;

public class EventSpecification {
	
	private EventSpecification() {}
	
	public static Specification<Events> hasLocation(String location) {
		return (root, query, cb) -> location == null ? null: cb.equal(cb.lower(root.get("location")), location.toLowerCase());
	}
	
	public static Specification<Events> titleContains(String keyword) {
		return (root, query, cb) -> keyword == null ? null : cb.equal(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
	}
	
	public static Specification<Events> priceBetween(Double minPrice, Double maxPrice) {
		return (root, query, cb) -> {
			if(minPrice == null && maxPrice == null) return null;
			if(minPrice != null && maxPrice != null) {
				return cb.between(root.get("price"), minPrice, maxPrice);
			}
			return minPrice != null ? cb.greaterThanOrEqualTo(root.get("price"), minPrice) :cb.lessThanOrEqualTo(root.get("price"), maxPrice);
		};
	}
	
	public static Specification<Events> dateBetween(String from, String to) {
		return (root, query, cb) -> {
			if(from == null && to == null) return null;
			if(from != null && to != null) {
				return cb.between(root.get("date"), from, to);
			}
			
			return from != null 
					? cb.greaterThanOrEqualTo(root.get("date"), from)
							: cb.lessThanOrEqualTo(root.get("date"), to);
		};
		
		
	}
}
