package com.github.tobish.yaws.http;

/**
 * Common HTTP-Response Header names
 */
public enum ResponseHeader {
	ETAG("ETag"),
	ACCEPT_RANGES("Accept-Ranges"),
	LAST_MODIFIED("Last-Modified"),
	SERVER("Server"),
	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	DATE("Date");
	
	private final String value;
	
	private ResponseHeader(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
