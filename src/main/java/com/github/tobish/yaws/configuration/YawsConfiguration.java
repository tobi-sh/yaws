package com.github.tobish.yaws.configuration;

import org.kohsuke.args4j.Option;

/**
 * The applications configuration. A pure data-structure therefore all variables are public
 * @author t.schulze
 *
 */
public class YawsConfiguration {

    @Option(name="-p",usage="The port where yaws will listen to")
	public int port = 8080;
	
    @Option(name="-d",usage="The root directory where all resources are located at")
	public String rootPath;
	
}
