package com.github.tobish.yaws;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ApplicationTest {
	
	
	
	@Test
	public void testApplicationReadConfigurationWhileCreating() {
		String[] args = {"-p", "8080"};
		Application app = new Application(args);
		
		assertThat(app.getConfiguration(), notNullValue());
		assertThat(app.getConfiguration().port, is(8080));
	}

}
