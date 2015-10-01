package com.github.tobish.yaws.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;


/**
 * Representation of a simple HTTP 1.1 request
 */
public class HttpRequest {
	
	private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);
	
	/**
	 * Parse the socket input stream and try to create a HttpRequest object. If this fails a parser 
	 * exception will be thrown.
	 * 
	 * @param reader A reader of the socket input stream
	 * @return A valid HttpRequest object
	 * @throws A ParserException if the request is not a valid HTTP Request
	 */
	public static HttpRequest parse(BufferedReader reader) {
		
		try {
			
			// The first line of a request defines the Method, URL and the protocol
			String requestLine = reader.readLine();
			String[] requestLineParts = requestLine.split(" ");
			
			Preconditions.checkArgument(requestLineParts.length == 3, 
					"The requestLine must contain of 3 space seperated parts: " + requestLine );
			
			String methodString = requestLineParts[0];
			String url = requestLineParts[1];
			String protocoll = requestLineParts[2];
			
			Preconditions.checkArgument(protocoll.matches("HTTP/1.[0,1]") , "Invalid protocoll passed: " + protocoll);

			Method method = Method.valueOf(methodString.toUpperCase());
			
			return new HttpRequest(url, method, ImmutableMap.<String, List<String>>of() , ImmutableMap.<String, List<String>>of(), "", "");

			
		} catch (IOException e) {
			LOG.error("Failed to parse request: ", e);
			throw new ParserException(e);
		}
		catch (NullPointerException npe) {
			LOG.error("Failed to parse request: ", npe);
			throw new ParserException(npe);
		}
		catch (IllegalArgumentException iae) {
			LOG.error("Failed to parse request: ", iae);
			throw new ParserException(iae);
		}
	}
	
	private HttpRequest(String url, Method method, Map<String, List<String>> header,
			Map<String, List<String>> queryStringParameter, String path, String messageBody) {
		super();
		this.url = url;
		this.method = method;
		this.header = header;
		this.queryStringParameter = queryStringParameter;
		this.path = path;
		this.messageBody = messageBody;
	}



	/**
	 * All HTTP Request Methods
	 */
	public enum Method {
		POST,
		GET,
		HEAD,
		PUT,
		DELETE,
		TRACE,
		CONNECT
	}
	
	/**
	 * The entire requested URL: path + query string
	 */
	private final String url;
	
	/**
	 * The requests method
	 */
	private final Method method;
	
	/**
	 * A collection of all request header. A header must have a unique identify (key) but
	 * can have multiple values
	 */
	private final Map<String, List<String>> header;
	
	/**
	 * The parameter part of the url. Each parameter must have a key but might 0 to n values
	 */
	private final Map<String, List<String>> queryStringParameter;
	
	/**
	 * The path of the URL
	 */
	private final String path;
	
	/**
	 * The message body of the request. Might be empty. But never null.
	 */
	private final String messageBody;

	public String getUrl() {
		return url;
	}

	public Method getMethod() {
		return method;
	}

	public Map<String, List<String>> getHeader() {
		return header;
	}

	public Map<String, List<String>> getQueryStringParameter() {
		return queryStringParameter;
	}

	public String getPath() {
		return path;
	}

	public String getMessageBody() {
		return messageBody;
	}
	
	
	
}
