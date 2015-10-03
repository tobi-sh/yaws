package com.github.tobish.yaws.http.methods;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.util.EtagProvider;

/**
 * Brute force implementation of HTTP HEAD Method (we will execute a GET and remove the content)
 *
 */
public class HttpHeadMethodHandler extends HttpGetMethodHandler {
	
	public HttpHeadMethodHandler(String documentRootDir, EtagProvider etagProvider) {
		super(documentRootDir, etagProvider);
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
