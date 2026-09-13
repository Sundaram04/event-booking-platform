package com.eventbooking.event_booking_platform.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import com.eventbooking.event_booking_platform.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		super();
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->{ 
						
						System.out.println(">>> ENTRY POINT HIT for " + request.getRequestURI()
					    + " | exception=" + authException.getClass().getSimpleName()
					    + " | message=" + authException.getMessage()
					    + " | contextAuth=" + SecurityContextHolder.getContext().getAuthentication());
				
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); }) )
				.authorizeHttpRequests(
						auth -> 
						auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers(HttpMethod.GET, "/events/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/events").hasAnyRole("ORGANIZER", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/events/**").hasAnyRole("ORGANIZER", "ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/events/**").hasAnyRole("ORGANIZER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/bookings/**").hasRole("USER")
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;

	}
	
	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
	        JwtAuthenticationFilter filter) {

	    FilterRegistrationBean<JwtAuthenticationFilter> registration =
	            new FilterRegistrationBean<>(filter);

	    registration.setEnabled(false);

	    return registration;
	}

}
