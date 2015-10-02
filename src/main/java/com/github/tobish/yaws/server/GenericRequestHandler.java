package com.github.tobish.yaws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.httpmethods.HttpMethodHandler;
import com.github.tobish.yaws.httpmethods.UnknownHttpMethodHandler;


/**
 * Handle each kind of request while only HTTP Requests will create proper result. Everything
 * else will result in a 405 response. This class will also care about the sate of the TCP-connection
 *
 */
public class GenericRequestHandler implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(GenericRequestHandler.class);
	
	private final Socket clientSocket;

	public GenericRequestHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

			HttpRequest httpRequest = HttpRequest.parse(reader);
			
			HttpMethodHandler methodHandler = buildMethodHandler(httpRequest);
			
			methodHandler.handleRequest(httpRequest, out);

			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HttpMethodHandler buildMethodHandler(HttpRequest httpRequest) {
		HttpMethodHandler methodHandler;
		
		switch (httpRequest.getMethod()) {
		case CONNECT:
		case DELETE:
		case GET:
		case HEAD:
		case POST:
		case PUT:
		case TRACE:
		default:
			methodHandler = new UnknownHttpMethodHandler();
		}
		return methodHandler;
	}

}
