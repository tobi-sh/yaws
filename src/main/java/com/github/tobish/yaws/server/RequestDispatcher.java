package com.github.tobish.yaws.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.configuration.YawsConfiguration;


final class RequestDispatcher implements Runnable {
	
	private static final int NUMBER_OF_THREADS = 3;

	public static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private final ServerSocket serverSocket;
	
	private final ExecutorService executorService ;
	
	private final YawsConfiguration configuration;

	RequestDispatcher(ServerSocket serverSocket, YawsConfiguration configuration) {
		this.serverSocket = serverSocket;
		this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		this.configuration = configuration;
	}

	@Override
	public void run() {
		while(!serverSocket.isClosed()) {
			try {
				Socket clientSocket = serverSocket.accept();
				executorService.execute(new GenericRequestHandler(clientSocket, configuration));
			} catch (IOException e) {
				break;
			}
		}
	}
}