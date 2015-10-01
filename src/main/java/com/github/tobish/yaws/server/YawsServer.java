package com.github.tobish.yaws.server;

import com.github.tobish.yaws.configuration.YawsConfiguration;

/**
 * 
 * @author t.schulze
 *
 */
public interface YawsServer {

	/**
	 * Start the server with a given configuration
	 */
	void start(YawsConfiguration configuration);
	
	/**
	 * Stop the server. Do not accept any request but answer all requests which 
	 * were accepted until now
	 */
	void stop();
}
