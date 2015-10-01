package com.github.tobish.yaws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


final class RequestDispatcher implements Runnable {
	
	public static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private final ServerSocket serverSocket;

	RequestDispatcher(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		while(!serverSocket.isClosed()) {
			try {
				Socket clientSocket = serverSocket.accept();
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			            new InputStreamReader(clientSocket.getInputStream()));

			    LOG.info("Recieved request:");
			    in.lines().forEach(LOG::info);
			    
			    out.write("OK");
			    out.flush();
			    clientSocket.close();
			    
			} catch (IOException e) {
				break;
			}
		}
	}
}