package com.github.tobish.yaws;

import org.junit.Test;

public class ApplicationTest {
	
	@Test
	public void testStartTheApplicationShouldOpenPort8080() {
		String[] args = {"8080"}; 
		Application app = new Application(args);
		app.start();
	}

}
