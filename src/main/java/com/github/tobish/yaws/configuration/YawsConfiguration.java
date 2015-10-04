package com.github.tobish.yaws.configuration;

import org.kohsuke.args4j.Option;

/**
 * The applications configuration. A pure data-structure therefore all variables are public
 *
 */
public class YawsConfiguration {

    @Option(name="-p",usage="The port where yaws will listen to")
	public int port = 8080;
	
    @Option(name="-d",usage="The root directory where all resources are located at")
	public String rootPath ;
    
    @Option(name="-t",usage="The keep-alive timeout for TCP connections in ms")
    public int connectionTimeout = 5000;
    
    @Option(name="-c",usage="The max number of client connections for this server")
    public int numberOfParalellConnections = 100;
	
}
