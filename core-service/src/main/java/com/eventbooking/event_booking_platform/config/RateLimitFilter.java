package com.eventbooking.event_booking_platform.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

	private Bucket newBucket() {
		Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
		return Bucket.builder().addLimit(limit).build();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (request.getRequestURI().equals("/api/v1/auth/login")) {
			String clientKey = request.getRemoteAddr();
			Bucket bucket = buckets.computeIfAbsent(clientKey, k -> newBucket());

			if (!bucket.tryConsume(1)) {
				response.setStatus(429);
				response.setHeader("Retry-After", "60");
				response.getWriter().write("{\"message\":\"Too many login attempts. Try again shortly.\"}");
				return;
			}

		}

		chain.doFilter(request, response);

	}

}
