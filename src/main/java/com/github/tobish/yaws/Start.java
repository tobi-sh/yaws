package com.github.tobish.yaws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application entry point
 * 
 * @author t.schulze
 *
 */
public class Start {

	public static final Logger LOG = LoggerFactory.getLogger(Start.class);
	
	public static void main(String[] args) {
		Application app = new Application(args);
		app.start();
	}
	
}
