package com.github.tobish.yaws.httpmethods;

import java.io.PrintWriter;

import com.github.tobish.yaws.http.HttpRequest;

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
	void handleRequest(HttpRequest request, PrintWriter output );
}
