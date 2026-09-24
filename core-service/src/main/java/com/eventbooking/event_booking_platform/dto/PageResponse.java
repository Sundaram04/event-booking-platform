package com.eventbooking.event_booking_platform.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class PageResponse<T> {
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean first;
	private boolean last;
	
	
	
	public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first,
			boolean last) {
		super();
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isFirst(),
				page.isLast()
				);
	}
	
	public List<T> getContent() { return content; }
	 public int getPage() { return page; }
	 public int getSize() { return size; }
	 public long getTotalElements() { return totalElements; }
	 public int getTotalPages() { return totalPages; }
	 public boolean isFirst() { return first; }
	 public boolean isLast() { return last; }
	
	

}
