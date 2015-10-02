package com.github.tobish.yaws.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


final class RequestDispatcher implements Runnable {
	
	private static final int NUMBER_OF_THREADS = 80;

	public static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private final ServerSocket serverSocket;
	
	private final ExecutorService executorService ; 

	RequestDispatcher(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
	}

	@Override
	public void run() {
		while(!serverSocket.isClosed()) {
			try {
				Socket clientSocket = serverSocket.accept();
				executorService.execute(new GenericRequestHandler(clientSocket));
				
			    
			} catch (IOException e) {
				break;
			}
		}
	}
}