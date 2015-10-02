package com.github.tobish.yaws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.configuration.YawsConfiguration;
import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.httpmethods.HttpGetMethodHandler;
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
	
	private final YawsConfiguration configuration;

	public GenericRequestHandler(Socket clientSocket, YawsConfiguration configuration) {
		this.clientSocket = clientSocket;
		this.configuration = configuration;
	}

	@Override
	public void run() {

		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			OutputStream out = clientSocket.getOutputStream();

			HttpRequest httpRequest = HttpRequest.parse(reader);
			
			HttpMethodHandler methodHandler = buildMethodHandler(httpRequest);
			
			HttpResponse response = methodHandler.handleRequest(httpRequest);
			
			out.write(response.toString().getBytes());
			out.write(response.getContent());
			
			out.close();

			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HttpMethodHandler buildMethodHandler(HttpRequest httpRequest) {
		HttpMethodHandler methodHandler;
		
		switch (httpRequest.getMethod()) {
		case GET:
			methodHandler = new HttpGetMethodHandler(configuration.rootPath);
			break;

		case CONNECT:
		case DELETE:
		case HEAD:
		case POST:
		case PUT:
		case TRACE:
		default:
			methodHandler = new UnknownHttpMethodHandler();
			break;
		}
		return methodHandler;
	}

}
