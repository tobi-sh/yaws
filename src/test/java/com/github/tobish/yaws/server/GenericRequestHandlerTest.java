package com.github.tobish.yaws.server;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mockito.Mockito;

import com.github.tobish.yaws.configuration.YawsConfiguration;

public class GenericRequestHandlerTest {

	@Test
	public void testIWillAlwaysGetAnAnswer() throws IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String sampleRequest = "GET /index.html HTTP/1.1" + System.lineSeparator();

		Socket clientSocket = mockSocket(outputStream, sampleRequest);
		YawsConfiguration configuration = new YawsConfiguration();
		configuration.rootPath = "/";

		GenericRequestHandler requestHandler = new GenericRequestHandler(clientSocket, configuration);

		requestHandler.run();

		String result = new String(outputStream.toByteArray());

		assertThat(result, not(isEmptyOrNullString()));
	}

	@Test
	public void testInvalidMethodWillReturnAMethodNotAllowedCode() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String sampleRequest = "CONNECT / HTTP/1.1" + System.lineSeparator();

		Socket clientSocket = mockSocket(outputStream, sampleRequest);
		YawsConfiguration configuration = new YawsConfiguration();
		configuration.rootPath = "/";
		
		GenericRequestHandler requestHandler = new GenericRequestHandler(clientSocket, configuration);

		requestHandler.run();

		String result = new String(outputStream.toByteArray());
		assertThat(result, containsString("501"));
	}

	private Socket mockSocket(ByteArrayOutputStream outputStream, String sampleRequest) throws IOException {
		Socket clientSocket = Mockito.mock(Socket.class);

		InputStream inputStream = new ByteArrayInputStream(sampleRequest.getBytes(StandardCharsets.UTF_8));

		Mockito.when(clientSocket.getInputStream()).thenReturn(inputStream);
		Mockito.when(clientSocket.getOutputStream()).thenReturn(outputStream);
		return clientSocket;
	}
}
