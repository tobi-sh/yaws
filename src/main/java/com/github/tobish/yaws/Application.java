package com.github.tobish.yaws;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.configuration.YawsConfiguration;
import com.github.tobish.yaws.server.YawsServer;
import com.github.tobish.yaws.server.YawsServerImpl;

/**
 * Handle the lifecycle of the application by allowing us starting and stoping
 * the server
 * 
 * @author t.schulze
 *
 */
public class Application {

	public static final Logger LOG = LoggerFactory.getLogger(Start.class);

	private final YawsConfiguration configuration = new YawsConfiguration();
	
	private final YawsServer server;

	/**
	 * Create a new application instance and parse the command line parameter.
	 * If this fails an exception will be thrown
	 * 
	 * @param args
	 */
	public Application(String[] args) {
		parseOptions(args);
		server = new YawsServerImpl();
	}

	public void start() {
		server.start(configuration);
	}

	public void stop() {
		server.stop();
	}

	public YawsConfiguration getConfiguration() {
		return configuration;
	}
	

	private void parseOptions(String[] args) {
		CmdLineParser parser = new CmdLineParser(configuration);
		try {
			parser.parseArgument(args);
			if (null == configuration.rootPath) {
				LOG.info("No document dir defined. Assuming {}", System.getProperty("user.dir"));
				configuration.rootPath = System.getProperty("user.dir");
			}
				
		} catch (CmdLineException e) {
			LOG.error("Failed to read command line parameters");
			LOG.info(parser.printExample(OptionHandlerFilter.ALL));
			throw new RuntimeException(e);
		}
	}


}
