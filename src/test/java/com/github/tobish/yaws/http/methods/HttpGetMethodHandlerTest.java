package com.github.tobish.yaws.http.methods;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpRequest.Method;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.constants.ResponseCode;
import com.github.tobish.yaws.http.methods.HttpGetMethodHandler;
import com.google.common.io.Files;

public class HttpGetMethodHandlerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setup() throws IOException {
		
		File indexHtml = folder.newFile("index.html");
		Files.write("some content".getBytes(), indexHtml);
		
		File fooHtml = folder.newFile("foo.html");
		Files.write("bar".getBytes(), fooHtml);
	}
	
	@Test
	public void testUnknownResourceShouldReturnANotFoundResponse() throws IOException {
		HttpRequest request = getRequestForUrl("/pizza.html");
		
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath());
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.NOT_FOUND));
	}

	@Test
	public void testDirectoryListing() {
		HttpRequest request = getRequestForUrl("/");
		
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath());
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.OK));
		assertThat(new String(response.getContent()), containsString("index.html"));
		assertThat(new String(response.getContent()), containsString("foo.html"));
	}
	
	@Test
	public void testRequestAValidResource() {
		HttpRequest request = getRequestForUrl("/index.html");
		
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath());
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.OK));
		assertThat(new String(response.getContent()), containsString("some content"));
	}
	

	private HttpRequest getRequestForUrl(String url) {
		HttpRequest request = new HttpRequest.HttpRequestBuilder()
				.withMethod(Method.GET)
				.withPath(url)
				.withUrl(url)
				.build();
		return request;
	}
	
	
}
