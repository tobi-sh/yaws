package com.github.tobish.yaws.server;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.github.tobish.yaws.configuration.YawsConfiguration;

public class YawsServerImplTest {

	@Test
	public void testServerStart() throws UnknownHostException, IOException {
		YawsConfiguration config = new YawsConfiguration();
		config.port = getRandomFreePort();
		config.rootPath = "/tmp";
		YawsServerImpl server = new YawsServerImpl();
		server.start(config);

		Socket client = new Socket("127.0.0.1", config.port);
		client.close();
		server.stop();
		
	}

	@Test(expected=ConnectException.class)
	public void testServerShutdown() throws UnknownHostException, IOException {
		YawsConfiguration config = new YawsConfiguration();
		config.port = getRandomFreePort();
		config.rootPath = "/tmp";
		YawsServerImpl server = new YawsServerImpl();
		server.start(config);

		Socket client = new Socket("127.0.0.1", config.port);
		client.close();

		server.stop();
		client = new Socket("127.0.0.1", config.port);
	}
	
	@Test
	public void testTcpConnectionIsNotClosedAfterOneRequest() throws UnknownHostException, IOException, InterruptedException {
		YawsConfiguration config = new YawsConfiguration();
		config.port = getRandomFreePort();
		config.rootPath = "/tmp";
		YawsServerImpl server = new YawsServerImpl();
		server.start(config);
		
		Socket client = new Socket("127.0.0.1", config.port);
		BufferedWriter out = new BufferedWriter( new OutputStreamWriter(  client.getOutputStream() ));
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), Charset.defaultCharset()));
		
		out.write("GET /index.html HTTP/1.1" + System.lineSeparator());
		out.write("Host: 127.0.0.1" + System.lineSeparator());
		out.write(System.lineSeparator());
		out.flush();
		
		String response = in.readLine();
		
		// never mind the concrete response
		assertThat(response, containsString("HTTP/1.1"));
		assertThat(client.isConnected(), Matchers.is(true));
		
	}
	
	private static int lastFreePort = 8000;

	private static final int getRandomFreePort() {
		for (int portOnTest= lastFreePort+1; portOnTest<Short.MAX_VALUE * 2 ; portOnTest++) {
			try {
				new Socket("127.0.0.1", portOnTest);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				lastFreePort  = portOnTest; 
				return portOnTest;
			}	
		}
		throw new RuntimeException("Unable to find any free ports");
	}

}
