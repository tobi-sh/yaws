package com.github.tobish.yaws.httpmethods;

import java.io.PrintWriter;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.HttpResponse.ResponseCode;

/**
 * Handle unknown http methods
 *
 */
public class UnknownHttpMethodHandler implements HttpMethodHandler {

	@Override
	public void handleRequest(HttpRequest request, PrintWriter output) {
		
		HttpResponse response = 
				new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.METHOD_NOT_ALLOWED).build();
		
		output.write(response.toString());
		output.flush();
	}

}
