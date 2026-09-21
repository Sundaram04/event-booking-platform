package com.eventbooking.event_booking_platform;

import java.util.Map;

public class ErrorResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	private Map<String, String> fieldErrors;
	
	public ErrorResponse(String timestamp, int status, String error, String message, String path,
			Map<String, String> fieldErrors) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
		this.fieldErrors = fieldErrors;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}
	
	
	

}
