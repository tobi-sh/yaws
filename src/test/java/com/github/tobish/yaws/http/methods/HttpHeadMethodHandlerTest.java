package com.github.tobish.yaws.http.methods;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpRequest.Method;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.constants.ResponseCode;
import com.github.tobish.yaws.http.methods.HttpHeadMethodHandler;
import com.google.common.io.Files;

public class HttpHeadMethodHandlerTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setup() throws IOException {
		File indexHtml = folder.newFile("index.html");
		Files.write("some content".getBytes(), indexHtml);
	}
	
	@Test
	public void testNoContentWillBeDelivered() {
		HttpRequest request = new HttpRequest.HttpRequestBuilder()
				.withMethod(Method.HEAD)
				.withPath("/index.html")
				.withUrl("/index.html")
				.build();
		
		HttpHeadMethodHandler headHandler = new HttpHeadMethodHandler(folder.getRoot().getAbsolutePath());
		HttpResponse response = headHandler.handleRequest(request);
		
		Assert.assertThat(response.getResponseCode(), Matchers.is(ResponseCode.OK));
		Assert.assertThat(response.getContent().length, Matchers.is(0));
	}

}
