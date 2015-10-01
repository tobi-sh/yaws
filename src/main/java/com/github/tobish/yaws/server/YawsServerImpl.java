package com.github.tobish.yaws.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.configuration.YawsConfiguration;

public class YawsServerImpl implements YawsServer {

	public static final Logger LOG = LoggerFactory.getLogger(YawsServerImpl.class);
	ServerSocket serverSocket;
	private ExecutorService singleThreadExecutor;

	
	@Override
	public void start(YawsConfiguration configuration) {
		try {
			LOG.info("Start listing on port {}", configuration.port);		
			serverSocket = new ServerSocket(configuration.port);
			
			singleThreadExecutor = Executors.newSingleThreadExecutor();
			singleThreadExecutor.execute(new RequestDispatcher(serverSocket));
			
		} catch (IOException e) {
			LOG.error("Failed to start a new server on port {}", configuration.port, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		LOG.info("Stopping the server");
		if (null != serverSocket && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
				singleThreadExecutor.shutdown();
			} catch (IOException e) {
				LOG.error("Failed to stop server", e);
				throw new RuntimeException(e);
			}
		}
	}

}
