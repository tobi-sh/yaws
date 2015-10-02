package com.github.tobish.yaws.http;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents a HTTP/1.1-Response
 *
 */
public class HttpResponse {

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
		
		SERVER_ERROR(500, "Internal Server Error")
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
	
	
	private static final String HTTP_VERSION = "HTTP/1.1";
	
	private final ResponseCode responseCode;
	
	private final Map<String, List<String>> header;
	
	private final byte[] content;

	private HttpResponse(ResponseCode responseCode, Map<String, List<String>> header, byte[] content) {
		super();
		this.responseCode = responseCode;
		this.header = header;
		this.content = content;
	}
	
	/**
	 * The string representation of the HTTP-Response. According to the spec this should look like:
	 * 
	 	Response      = HTTP-Version SP Status-Code SP Reason-Phrase CRLF               
                       *(( general-header        
                        | response-header        
                        | entity-header ) CRLF)  
                       CRLF
                       [ message-body ]           
	 * 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		addStatusLine(sb);
		addHeader(sb);
		addContent(sb);
		
		return sb.toString();
	}

	private void addContent(StringBuffer sb) {
		if (content.length > 0) {
			sb.append(System.lineSeparator());
			sb.append(new String(content));			
		}
	}

	private void addHeader(StringBuffer sb) {
		header.forEach((String key, List<String> value) -> {
			sb.append(key);
			sb.append(":");
			sb.append(value);
			sb.append(System.lineSeparator());
		});
		
	}

	private void addStatusLine(StringBuffer sb) {
		sb.append(HTTP_VERSION);
		sb.append(" ");
		sb.append(responseCode.getResponseCode());
		sb.append(" ");
		sb.append(responseCode.getResponseText());
		sb.append(System.lineSeparator());
	}
	
	
	/**
	 * Fluent interface builder for HTTP Responses
	 *
	 */
	public static class HttpResonseBuilder {
		
		private  ResponseCode responseCode;
		
		private  Map<String, List<String>> header = Maps.<String, List<String>>newHashMap();
		
		private byte[] content = new byte[0];
		
		public HttpResonseBuilder withResponseCode(ResponseCode code) {
			this.responseCode = code;
			return this;
		}
		
		public HttpResonseBuilder withHeader(Map<String, List<String>> header ) {
			this.header = header;
			return this;
		}
		
		public HttpResonseBuilder addHeader(String key, String value) {
			if (!header.containsKey(key)) {
				header.put(key, Lists.<String>newArrayList());
			}
			header.get(key).add(value);
			return this;
		}
		
		public HttpResonseBuilder addContent(byte[] content) {
			this.content = content;
			return this;
		}
		
		public HttpResponse build() {
			Preconditions.checkNotNull(responseCode);
			
			return new HttpResponse(responseCode, header, content);
		}

	}
	
}
