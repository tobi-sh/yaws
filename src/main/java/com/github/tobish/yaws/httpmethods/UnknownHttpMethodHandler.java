package com.github.tobish.yaws.httpmethods;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.constants.ResponseCode;

/**
 * Handle unknown http methods
 *
 */
public class UnknownHttpMethodHandler implements HttpMethodHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		
		HttpResponse response = 
				new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.SERVICE_NOT_IMPLEMENTED).build();
		
		return response;
	}

}
