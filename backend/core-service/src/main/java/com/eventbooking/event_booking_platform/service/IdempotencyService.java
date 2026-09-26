package com.eventbooking.event_booking_platform.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event_booking_platform.entity.IdempotencyKey;
import com.eventbooking.event_booking_platform.repository.IdempotencyKeyRepository;

@Service
public class IdempotencyService {
	private final IdempotencyKeyRepository repository;
	private final IdempotencyService self;

	public IdempotencyService(IdempotencyKeyRepository repository, @Lazy IdempotencyService self) {
		super();
		this.repository = repository;
		this.self = self;
	}

	/**
	 * Not itself transactional: insertPlaceholder() and findExisting() each run in their own
	 * REQUIRES_NEW transaction (invoked through the proxy via {@code self}, not this.method(),
	 * since self-invocation bypasses Spring's transactional advice). A failed insert marks the
	 * transaction it ran in as rollback-only; isolating it in its own transaction means the
	 * recovery read below runs in a fresh, healthy transaction instead of throwing
	 * UnexpectedRollbackException.
	 */
	public Optional<IdempotencyKey> claim(String key, String requestHash) {
		try {
			self.insertPlaceholder(key, requestHash);
			return Optional.empty();
		} catch (DataIntegrityViolationException ex) {
			return self.findExisting(key);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertPlaceholder(String key, String requestHash) {
		IdempotencyKey placeholder = new IdempotencyKey(key, requestHash, 0, null, Instant.now());
		repository.saveAndFlush(placeholder);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Optional<IdempotencyKey> findExisting(String key) {
		return repository.findById(key);
	}

	@Transactional
	public void complete(String key, int status, String responseBody) {
		IdempotencyKey row = repository.findById(key)
				.orElseThrow(() -> new IllegalStateException("Idempotency key vansihed: " + key));
		row.setResponseStatus(status);
		row.setResponseBody(responseBody);
		repository.save(row);

	}

	@Transactional
	public void release(String key) {
		repository.deleteById(key);
	}

}
