package com.github.tobish.yaws.httpmethods;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;

/**
 * Brute force implementation of HTTP HEAD Method (we will execute a GET and remove the content)
 *
 */
public class HttpHeadMethodHandler extends HttpGetMethodHandler {
	
	public HttpHeadMethodHandler(String documentRootDir) {
		super(documentRootDir);
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		HttpResponse getResponse = super.handleRequest(request);
		
		HttpResponse response = new HttpResponse.HttpResonseBuilder()
				.withHeader(getResponse.getHeader())
				.withResponseCode(getResponse.getResponseCode())
				.build();
		
		return response;
	}

}
