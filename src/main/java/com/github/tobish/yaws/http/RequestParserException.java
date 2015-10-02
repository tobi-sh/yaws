package com.github.tobish.yaws.http;

public class RequestParserException extends RuntimeException {

	public RequestParserException(Throwable e) {
		super(e);
	}
	
	public RequestParserException(String message, Throwable e) {
		super(message, e);
	}
}
