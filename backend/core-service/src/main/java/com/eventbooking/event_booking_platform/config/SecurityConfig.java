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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.springframework.security.config.Customizer.withDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private final RateLimitFilter rateLimitFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, RateLimitFilter rateLimitFilter) {

		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.rateLimitFilter = rateLimitFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.headers(headers -> headers.frameOptions(frame -> frame.deny()).contentTypeOptions(withDefaults())
						.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))

				).exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {

					log.debug("Unauthenticated access to {}: {}", request.getRequestURI(), authException.getMessage());

					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				}))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health").permitAll()
						.requestMatchers("/actuator/**").hasRole("ADMIN")

						.requestMatchers("/api/v1/auth/**").permitAll()

						.requestMatchers("/error").permitAll().requestMatchers(HttpMethod.GET, "/api/v1/events/**")
						.authenticated().requestMatchers(HttpMethod.POST, "/api/v1/events")
						.hasAnyRole("ORGANIZER", "ADMIN").requestMatchers(HttpMethod.PUT, "/api/v1/events/**")
						.hasAnyRole("ORGANIZER", "ADMIN").requestMatchers(HttpMethod.DELETE, "/api/v1/events/**")
						.hasAnyRole("ORGANIZER", "ADMIN").requestMatchers(HttpMethod.POST, "/api/v1/bookings/**")
						.hasRole("USER").anyRequest().authenticated())
				.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
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

		FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);

		registration.setEnabled(false);

		return registration;
	}

}
