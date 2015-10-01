package com.github.tobish.yaws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle the lifecycle of the application by allowing us starting and stoping the server
 * 
 * @author t.schulze
 *
 */
public class Application {

	public static final Logger LOG = LoggerFactory.getLogger(Start.class);
	
	/**
	 * Create a new application instance and parse the command line parameter. If 
	 * this fails an exception will be thrown
	 * 
	 * @param args
	 */
	public Application(String[] args) {
	}
	
	
	public void start() {
		LOG.info("You have to start somewhere");
	}
	
	public void stop() {	
	}

}
