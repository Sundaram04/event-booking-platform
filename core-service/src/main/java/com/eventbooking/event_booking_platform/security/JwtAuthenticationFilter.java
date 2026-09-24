package com.eventbooking.event_booking_platform.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		super();
		this.jwtService = jwtService;
	}
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
			) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = authHeader.substring(7);

		boolean valid = jwtService.isTokenValid(token);

		if (valid) {

		    String email = jwtService.extractEmail(token);
		    String role = jwtService.extractRole(token);
		    Long userId = jwtService.extractUserId(token);

		    List<GrantedAuthority> authorities =
		            List.of(new SimpleGrantedAuthority("ROLE_" + role));

		    UsernamePasswordAuthenticationToken authToken =
		            new UsernamePasswordAuthenticationToken(
		                    email,
		                    null,
		                    authorities
		            );
		    authToken.setDetails(userId);

		    SecurityContextHolder.getContext().setAuthentication(authToken);

		    log.debug("Authenticated {} via JWT for {}", email, request.getRequestURI());
		}
		
		
		filterChain.doFilter(request, response);
	}
	

}
