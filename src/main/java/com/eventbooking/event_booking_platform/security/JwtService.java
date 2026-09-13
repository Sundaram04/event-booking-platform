package com.eventbooking.event_booking_platform.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.model.Role;

import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	private final SecretKey secretKey = Jwts.SIG.HS256.key().build();
	private final long expirationMs = 1000 * 60 * 60;
	
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

