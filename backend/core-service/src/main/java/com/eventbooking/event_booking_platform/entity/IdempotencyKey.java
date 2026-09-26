package com.eventbooking.event_booking_platform.entity;

import java.time.Instant;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * The id ({@code requestkey}) is client-supplied, not {@code @GeneratedValue}, so without
 * implementing {@link Persistable}, Spring Data JPA's default isNew() check (id == null) always
 * evaluates false and routes every save() through entityManager.merge() (an upsert) instead of
 * persist() (an atomic insert). That silently overwrites an existing row on a reused key instead
 * of failing with a unique-constraint violation, defeating the idempotency claim mechanism.
 */
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey implements Persistable<String> {
	@Id
	private String requestkey;
	private String requestHash;
	private int responseStatus;
	@Lob
	private String responseBody;
	private Instant createdAt;

	@Transient
	private boolean isNew = true;

	protected IdempotencyKey() {
	}

	public IdempotencyKey(String requestkey, String requestHash, int responseStatus, String responseBody,
			Instant createdAt) {
		super();
		this.requestkey = requestkey;
		this.requestHash = requestHash;
		this.responseStatus = responseStatus;
		this.responseBody = responseBody;
		this.createdAt = createdAt;
		this.isNew = true;
	}

	@Override
	public String getId() {
		return requestkey;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@PostPersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}

	public String getRequestkey() {
		return requestkey;
	}

	public String getRequestHash() {
		return requestHash;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setRequestkey(String requestkey) {
		this.requestkey = requestkey;
	}

	public void setRequestHash(String requestHash) {
		this.requestHash = requestHash;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

}
