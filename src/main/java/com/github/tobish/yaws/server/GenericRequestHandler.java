package com.github.tobish.yaws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.configuration.YawsConfiguration;
import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpRequest.Method;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.RequestParserException;
import com.github.tobish.yaws.http.constants.RequestHeader;
import com.github.tobish.yaws.http.methods.HttpGetMethodHandler;
import com.github.tobish.yaws.http.methods.HttpHeadMethodHandler;
import com.github.tobish.yaws.http.methods.HttpMethodHandler;
import com.github.tobish.yaws.http.methods.UnknownHttpMethodHandler;
import com.github.tobish.yaws.util.Md5EtagProvider;

/**
 * Handle each kind of request while only HTTP Requests will create proper
 * result. Everything else will result in a 405 response. This class will also
 * care about the sate of the TCP-connection
 *
 */
public class GenericRequestHandler implements Runnable {

	private static final String CONNECTION_CLOSE_HEADER_VALUE = "close";

	private static final Logger LOG = LoggerFactory.getLogger(GenericRequestHandler.class);

	private final Socket clientSocket;

	private final YawsConfiguration configuration;
	
	private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

	public GenericRequestHandler(Socket clientSocket, YawsConfiguration configuration) {
		this.clientSocket = clientSocket;
		this.configuration = configuration;
	}

	@Override
	public void run() {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			OutputStream out = clientSocket.getOutputStream();

			while (!clientSocket.isClosed()) {

				Future<HttpRequest> futureRequest = singleThreadExecutor.submit(new HttpRequestParser(reader));
				Optional<HttpRequest> optinalHttpRequest = parseRequest(futureRequest);

				// leave if the request could not be parsed after
				// KEEP_ALIVE_TIMEOUT ms
				if (!optinalHttpRequest.isPresent()) {
					clientSocket.close();
					break;
				}
				HttpRequest httpRequest = optinalHttpRequest.get();

				HttpMethodHandler methodHandler = buildMethodHandler(httpRequest);

				HttpResponse response = methodHandler.handleRequest(httpRequest);

				writeResponse(out, response);

				out.flush();

				if (!isPersistentConnection(httpRequest)) {
					clientSocket.close();
					break;
				}
			}

		} catch (IOException e) {
			LOG.error("Error while reading/writing from socket ", e);
		}

	}

	/**
	 * Decide if the TCP Connection should be kept open
	 * 
	 * @param httpRequest
	 * @return
	 */
	private boolean isPersistentConnection(HttpRequest httpRequest) {
		List<String> connectionHeader = httpRequest.getHeader().getOrDefault(RequestHeader.CONNECTION.toString(), Collections.EMPTY_LIST);
		connectionHeader.contains(CONNECTION_CLOSE_HEADER_VALUE);
		return !connectionHeader.contains(CONNECTION_CLOSE_HEADER_VALUE) && httpRequest.getMethod() != Method.UNKNOWN;
	}

	private Optional<HttpRequest> parseRequest(Future<HttpRequest> futureRequest) {
		HttpRequest httpRequest = null;
		try {
			httpRequest = futureRequest.get(configuration.connectionTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.error("Interupted while parsing request ", e);
			throw new RequestParserException(e);
		} catch (ExecutionException e) {
			LOG.error("Failed to parse request ", e);
			throw new RequestParserException(e);
		} catch (TimeoutException e) {
			// Thats not that unusual - just return an absent optional
		}
		return Optional.ofNullable(httpRequest);
	}

	private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
		out.write(response.toString().getBytes()); // The Respone-Line and the
													// Response-Header
		out.write(System.lineSeparator().getBytes()); // Seperate the content by
														// an additional line
														// break
		out.write(response.getContent());
	}

	private HttpMethodHandler buildMethodHandler(HttpRequest httpRequest) {
		HttpMethodHandler methodHandler;

		switch (httpRequest.getMethod()) {
		case GET:
			methodHandler = new HttpGetMethodHandler(configuration.rootPath, new Md5EtagProvider());
			break;
		case HEAD:
			methodHandler = new HttpHeadMethodHandler(configuration.rootPath, new Md5EtagProvider());
			break;

		case CONNECT:
		case DELETE:
		case POST:
		case PUT:
		case TRACE:
		default:
			methodHandler = new UnknownHttpMethodHandler(); // will just return
															// a 501
			break;
		}
		return methodHandler;
	}

	/**
	 * Wrap HttpRequest.parse in a callable to execute in a seperate thread
	 */
	private final class HttpRequestParser implements Callable<HttpRequest> {

		private final BufferedReader reader;

		public HttpRequestParser(BufferedReader reader) {
			super();
			this.reader = reader;
		}

		@Override
		public HttpRequest call() throws Exception {
			return HttpRequest.parse(reader);
		}
	}

}
