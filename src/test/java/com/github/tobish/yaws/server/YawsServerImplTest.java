package com.github.tobish.yaws.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import com.github.tobish.yaws.configuration.YawsConfiguration;

public class YawsServerImplTest {

	@Test
	public void testServerStart() throws UnknownHostException, IOException {
		YawsConfiguration config = new YawsConfiguration();
		config.port = 8080;
		config.rootPath = "/tmp";
		YawsServerImpl server = new YawsServerImpl();
		server.start(config);

		Socket client = new Socket("127.0.0.1", 8080);
		client.close();
		server.stop();
		
	}

	@Test(expected=ConnectException.class)
	public void testServerShutdown() throws UnknownHostException, IOException {
		YawsConfiguration config = new YawsConfiguration();
		config.port = 8081;
		config.rootPath = "/tmp";
		YawsServerImpl server = new YawsServerImpl();
		server.start(config);

		Socket client = new Socket("127.0.0.1", 8081);
		client.close();

		server.stop();
		client = new Socket("127.0.0.1", 8081);

	}

}
