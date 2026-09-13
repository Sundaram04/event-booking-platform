package com.eventbooking.event_booking_platform.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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

		System.out.println(">>> JWT FILTER: " + request.getRequestURI());

		boolean valid = jwtService.isTokenValid(token);

		System.out.println(">>> TOKEN VALID: " + valid);

		if (valid) {

		    String email = jwtService.extractEmail(token);
		    String role = jwtService.extractRole(token);

		    System.out.println(">>> EMAIL: " + email);
		    System.out.println(">>> ROLE: " + role);

		    List<GrantedAuthority> authorities =
		            List.of(new SimpleGrantedAuthority("ROLE_" + role));

		    System.out.println(">>> AUTHORITIES: " + authorities);

		    UsernamePasswordAuthenticationToken authToken =
		            new UsernamePasswordAuthenticationToken(
		                    email,
		                    null,
		                    authorities
		            );

		    SecurityContextHolder.getContext().setAuthentication(authToken);

		    System.out.println(
				    ">>> BEFORE CHAIN AUTH: " +
				    SecurityContextHolder.getContext().getAuthentication()
				);
		}
		
		
		filterChain.doFilter(request, response);
	}
	

}
