package com.github.tobish.yaws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

final class RequestDispatcher implements Runnable {
	
	private final ServerSocket serverSocket;

	/**
	 * @param yawsServerImpl
	 */
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

			    YawsServerImpl.LOG.info("Recieved request:\n {}", in.readLine());
			    out.write("OK");
			    clientSocket.close();
			    
			} catch (IOException e) {
				break;
			}
		}
	}
}