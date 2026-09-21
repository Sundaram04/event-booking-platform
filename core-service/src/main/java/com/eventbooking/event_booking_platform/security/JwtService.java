package com.eventbooking.event_booking_platform.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.model.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey secretKey ;
	private final long expirationMs ;
	
	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-ms}") long expirationMs
			) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));	
		this.expirationMs = expirationMs;
	}
	
	
	
	public String generateToken(String email, Role role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);
		
		return Jwts
				.builder()
				.subject(email)
				.claim("role", role.name())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(secretKey)
				.compact();
	}
	
	public String extractEmail(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public String extractRole(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
	}
	
	public boolean isTokenValid(String token) {
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			return true;
		} catch(Exception e) {
			 e.printStackTrace();
		        return false;
			
		}
	}
}

