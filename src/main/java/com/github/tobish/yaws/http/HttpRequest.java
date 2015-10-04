package com.github.tobish.yaws.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


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
	public static final HttpRequest parse(BufferedReader reader) {
		
		try {
			
			HttpRequestBuilder builder = new HttpRequestBuilder();
			
			// The first line of a request defines the Method, URL and the protocol
			String requestLine = reader.readLine();
			
			// If the very first line is already empty it cannot be a valid HTTP-Request so exit early
			if (null == requestLine || requestLine.isEmpty() ) {
				return INVALID_HTTP_REQUEST;
			}
			parseRequestLine(builder, requestLine);
			
			// read HTTP-Header until the next empty line
			for (String line = reader.readLine(); null != line && !line.isEmpty() ; line = reader.readLine()) {
				builder.addHeader(line);
			}			
			
			
			return builder.build();
		}
		catch (SocketException e) {
			return INVALID_HTTP_REQUEST;
		}
		catch (Throwable e) {
			LOG.error("Failed to parse request", e);
			return INVALID_HTTP_REQUEST;
		}
	}
	
	public static final HttpRequest INVALID_HTTP_REQUEST = new HttpRequest("", Method.UNKNOWN, Collections.EMPTY_MAP, Collections.EMPTY_MAP, "", "");

	private static void parseRequestLine(HttpRequestBuilder builder, String requestLine) {
		String[] requestLineParts = requestLine.split(" ");
		
		Preconditions.checkArgument(requestLineParts.length == 3, 
				"The requestLine must contain of 3 space seperated parts: " + requestLine );
		
		String methodString = requestLineParts[0];
		Method method = Method.valueOf(methodString.toUpperCase());
		builder.withMethod(method);
		
		String url = requestLineParts[1];
		builder.withUrl(url);
		
		builder.withPath(url.split("\\?")[0]);
		
		String protocoll = requestLineParts[2];
		Preconditions.checkArgument(protocoll.matches("HTTP/1.[0,1]") , "Invalid protocoll passed: " + protocoll);
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
		CONNECT,
		UNKNOWN
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
	
	
	
	public static class HttpRequestBuilder {

		private String url;
		private Method method;
		private Map<String, List<String>> header = Maps.newHashMap();
		private Map<String, List<String>> queryStringParameter = Maps.newHashMap();
		private String path;
		private String messageBody = "";
		
		public HttpRequestBuilder withUrl(String url) {
			this.url = url;
			return this;
		}
		
		public HttpRequestBuilder withMethod(Method method) {
			this.method = method;
			return this;
		}
		
		public HttpRequestBuilder withHeader(Map<String, List<String>> header) {
			this.header = header;
			return this;
		}
		
		public HttpRequestBuilder addHeader(String header) {
			String[] headerParts = header.split(":");
			
			String key = headerParts[0];
			String value = headerParts.length > 1 ? headerParts[1].trim() : "";
			return this.addHeader(key, value);
		}
		
		public HttpRequestBuilder addHeader(String key,String value) {
			if (null == header) {
				header = Maps.<String, List<String>>newHashMap();
			}
			if (!header.containsKey(key)) {
				header.put(key, Lists.<String>newArrayList());
			}
			header.get(key).add(value);
			return this;
		}
		
		public HttpRequestBuilder withQueryStringParameter(Map<String, List<String>> parameter) {
			this.queryStringParameter = parameter;
			return this;
		}
		
		public HttpRequestBuilder addQueryStrinParameter(String key,String value) {
			if (null == queryStringParameter) {
				queryStringParameter = Maps.<String, List<String>>newHashMap();
			}
			if (!queryStringParameter.containsKey(key)) {
				queryStringParameter.put(key, Lists.<String>newArrayList());
			}
			queryStringParameter.get(key).add(value);
			return this;
		}
		
		public HttpRequestBuilder withPath(String p) {
			this.path = p;
			return this;
		}
		
		public HttpRequestBuilder withMessageBody(String body) {
			this.messageBody = body;
			return this;
		}
		
		public HttpRequest build() {
			checkNotNull(this.header);
			checkNotNull(this.messageBody);
			checkNotNull(this.method);
			checkNotNull(this.queryStringParameter);
			checkNotNull(this.url);
			
			return new HttpRequest(url, method, header, queryStringParameter, path, messageBody);
		}

	}
	
}
