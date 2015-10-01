package com.github.tobish.yaws.http;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

public class HttpRequestTest {

	@Test
	public void testParseRequestLine() {
		HttpRequest request = HttpRequest.parse(new BufferedReader( new StringReader("GET /index.html HTTP/1.1")));
		
		assertThat(request, notNullValue());
		assertThat(request.getMethod(), is(HttpRequest.Method.GET));
		assertThat(request.getUrl(), is("/index.html"));
		
	}
	
	@Test(expected=ParserException.class)
	public void testParseInvalidMethod() {
		HttpRequest.parse(new BufferedReader( new StringReader("PIZZA /index.html HTTP/1.1")));
	}
	
	@Test(expected=ParserException.class)
	public void testParseInvalidProtocol() {
		HttpRequest.parse(new BufferedReader( new StringReader("GET /index.html HTTP/0.9")));
	}
	
	@Test(expected=ParserException.class)
	public void testInvalidRequest() {
		HttpRequest.parse(new BufferedReader( new StringReader("I have no clue what I should pass here")));
	}
	
}
