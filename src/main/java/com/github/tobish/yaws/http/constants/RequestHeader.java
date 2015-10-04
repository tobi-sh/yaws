package com.github.tobish.yaws.http.constants;

/**
 * Common HTTP-Request Header names
 */
public enum RequestHeader {
	HOST("Host"),
	USER_AGENT("User-Agent"),
	KEEP_ALIVE("Keep-Alive"),
	IF_NONE_MATCH("If-None-Match"),
	IF_MODIFIED_SINCE ("If-Modified-Since"),
	CONNECTION("Connection"),
	IF_MATCH ("If-Match");
	
	private final String value;
	
	private RequestHeader(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
