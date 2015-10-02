package com.github.tobish.yaws.http;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;

import org.hamcrest.Matchers;
import org.junit.Test;

public class HttpRequestTest {

	@Test
	public void testParseRequestLine() {
		HttpRequest request = HttpRequest.parse(new BufferedReader( new StringReader("GET /index.html HTTP/1.1")));
		
		assertThat(request, notNullValue());
		assertThat(request.getMethod(), is(HttpRequest.Method.GET));
		assertThat(request.getUrl(), is("/index.html"));
		
	}
	
	@Test(expected=RequestParserException.class)
	public void testParseInvalidMethod() {
		HttpRequest.parse(new BufferedReader( new StringReader("PIZZA /index.html HTTP/1.1")));
	}
	
	@Test(expected=RequestParserException.class)
	public void testParseInvalidProtocol() {
		HttpRequest.parse(new BufferedReader( new StringReader("GET /index.html HTTP/0.9")));
	}
	
	@Test(expected=RequestParserException.class)
	public void testInvalidRequest() {
		HttpRequest.parse(new BufferedReader( new StringReader("I have no clue what I should pass here")));
	}
	
	@Test
	public void testParseHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("GET /index.html HTTP/1.1");
		sb.append(System.lineSeparator());
		sb.append("Host: localhost:8080");
		sb.append(System.lineSeparator());
		sb.append("User-Agent: curl/7.43.0");
		sb.append(System.lineSeparator());
		sb.append("foo: bar");
		sb.append(System.lineSeparator());
		sb.append("foo: baz");
		
		
		HttpRequest request = HttpRequest.parse(new BufferedReader( new StringReader(sb.toString())));
		
		assertThat(request.getHeader(), Matchers.hasKey("Host"));
		assertThat(request.getHeader(), Matchers.hasKey("User-Agent"));
		assertThat(request.getHeader(), Matchers.hasKey("foo"));
	}
	
}
