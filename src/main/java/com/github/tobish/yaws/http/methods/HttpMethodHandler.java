package com.github.tobish.yaws.http.methods;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;

/**
 * Handle the given HttpRequest and write the result to the output stream
 */
public interface HttpMethodHandler {
	
	/**
	 * Process the given request 
	 * 
	 * @param request
	 * @param output
	 */
	HttpResponse handleRequest(HttpRequest request);
}
