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
import com.github.tobish.yaws.http.methods.HttpGetMethodHandler;
import com.github.tobish.yaws.http.methods.HttpHeadMethodHandler;
import com.github.tobish.yaws.http.methods.HttpMethodHandler;
import com.github.tobish.yaws.http.methods.UnknownHttpMethodHandler;


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
			
			writeResponse(out, response);
			
			out.close();
			clientSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
		out.write(response.toString().getBytes());  // The Respone-Line and the Response-Header
		out.write(System.lineSeparator().getBytes()); // Seperate the content by an additional line break
		out.write(response.getContent());
	}

	private HttpMethodHandler buildMethodHandler(HttpRequest httpRequest) {
		HttpMethodHandler methodHandler;
		
		switch (httpRequest.getMethod()) {
		case GET:
			methodHandler = new HttpGetMethodHandler(configuration.rootPath);
			break;
		case HEAD:
			methodHandler = new HttpHeadMethodHandler(configuration.rootPath);
			break;
			
		case CONNECT:
		case DELETE:
		case POST:
		case PUT:
		case TRACE:
		default:
			methodHandler = new UnknownHttpMethodHandler(); // will just return a 501
			break;
		}
		return methodHandler;
	}

}
