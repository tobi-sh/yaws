package com.github.tobish.yaws.http.constants;

/**
 * HTTP Response codes with text and numerical representation
 */
public enum ResponseCode {
	OK (200, "OK"),
	CREATED(201, "Created"),
	
	BAD_REQUEST(400, "Bad Request"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	I_AM_A_TEAPOT(418, "I am a teapot"),
	
	SERVER_ERROR(500, "Internal Server Error"),
	SERVICE_NOT_IMPLEMENTED(501, "Not Implemented")
	;
	
	
	private final int code;
	
	private final String responseText;
	
	ResponseCode(int code, String text) {
		this.code = code;
		this.responseText = text;
	}
	
	public int getResponseCode() {
		return this.code;
	}
	
	public String getResponseText() {
		return this.responseText;
	}
}