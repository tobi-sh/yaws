package com.github.tobish.yaws.http.methods;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpRequest.Method;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.constants.RequestHeader;
import com.github.tobish.yaws.http.constants.ResponseCode;
import com.github.tobish.yaws.http.constants.ResponseHeader;
import com.github.tobish.yaws.util.EtagProvider;
import com.github.tobish.yaws.util.Md5EtagProvider;
import com.google.common.io.Files;

public class HttpGetMethodHandlerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	// always return "42" as the etag for any content
	public EtagProvider fixedEtagProvier;

	@Before
	public void setup() throws IOException {

		File indexHtml = folder.newFile("index.html");
		Files.write("some content".getBytes(), indexHtml);
		indexHtml.setLastModified( LocalDateTime.now().minusDays(2).toEpochSecond(ZoneOffset.UTC));


		File fooHtml = folder.newFile("foo.html");
		Files.write("bar".getBytes(), fooHtml);
		
		fixedEtagProvier = mock(EtagProvider.class);
		Mockito.when(fixedEtagProvier.provideEtag(Mockito.any())).thenReturn("\"42\"");
	}

	@Test
	public void testUnknownResourceShouldReturnANotFoundResponse() throws IOException {
		HttpRequest request = getRequestForUrl("/pizza.html");

		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),
				new Md5EtagProvider());
		HttpResponse response = getHandler.handleRequest(request);

		assertThat(response.getResponseCode(), is(ResponseCode.NOT_FOUND));
	}

	@Test
	public void testDirectoryListing() {
		HttpRequest request = getRequestForUrl("/");

		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),
				new Md5EtagProvider());
		HttpResponse response = getHandler.handleRequest(request);

		assertThat(response.getResponseCode(), is(ResponseCode.OK));
		assertThat(new String(response.getContent()), containsString("index.html"));
		assertThat(new String(response.getContent()), containsString("foo.html"));
	}

	@Test
	public void testRequestAValidResource() {
		HttpRequest request = getRequestForUrl("/index.html");

		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),
				new Md5EtagProvider());
		HttpResponse response = getHandler.handleRequest(request);

		assertThat(response.getResponseCode(), is(ResponseCode.OK));
		assertThat(new String(response.getContent()), containsString("some content"));
	}

	@Test
	public void testKnownResourcesShouldAlwaysIncludeAnEtagHeader() {

		HttpRequest request = getRequestForUrl("/index.html");

		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),
				new Md5EtagProvider());
		HttpResponse response = getHandler.handleRequest(request);

		assertThat(response.getHeader(), Matchers.hasKey(ResponseHeader.ETAG.toString()));

	}

	@Test
	public void testRequestWithAIfNotModifiedHeaderShouldReturnA304() {
		HttpRequest request = getRequestForUrlAndEtag("/index.html", "\"42\"");
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),fixedEtagProvier);
		
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.NOT_MODIFIED));
	}
	
	@Test
	public void testRequestWithANewerModifiedSinceDateShouldReturn304() {
		HttpRequest request = getRequestForUrlAndModifedDate("/index.html", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) );
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),fixedEtagProvier);
		
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.NOT_MODIFIED));
	}
	
	@Test
	public void testRequestWithAnOlderModifiedSinceDateShouldReturn200() {
		HttpRequest request = getRequestForUrlAndModifedDate("/index.html", LocalDateTime.now().minusDays(10).format(DateTimeFormatter.ISO_DATE_TIME) );
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),fixedEtagProvier);
		
		HttpResponse response = getHandler.handleRequest(request);
		
		assertThat(response.getResponseCode(), is(ResponseCode.OK));
	}
	
	@Test
	public void testIfMatchHeaderShouldCheckPrecondition() {
		HttpRequest request = getRequestForUrlAndCondition("/index.html", "99" );
		HttpGetMethodHandler getHandler = new HttpGetMethodHandler(folder.getRoot().getAbsolutePath(),fixedEtagProvier);
		
		HttpResponse response = getHandler.handleRequest(request);

		assertThat(response.getResponseCode(), is(ResponseCode.PRECONDITION_FAILED));
	}

	private HttpRequest getRequestForUrlAndCondition(String url, String condition) {
		HttpRequest request = new HttpRequest.HttpRequestBuilder()
				.withMethod(Method.GET)
				.withPath(url)
				.withUrl(url)
				.addHeader(RequestHeader.IF_MATCH.toString(), condition)
				.build();
		return request;
	}

	private HttpRequest getRequestForUrl(String url) {
		HttpRequest request = new HttpRequest.HttpRequestBuilder().withMethod(Method.GET).withPath(url).withUrl(url)
				.build();
		return request;
	}

	private HttpRequest getRequestForUrlAndEtag(String url, String etag) {
		HttpRequest request = new HttpRequest.HttpRequestBuilder()
				.withMethod(Method.GET)
				.withPath(url)
				.withUrl(url)
				.addHeader(RequestHeader.IF_NONE_MATCH.toString(), etag)
				.build();
		return request;
	}
	
	
	private HttpRequest getRequestForUrlAndModifedDate(String url, String date) {
		HttpRequest request = new HttpRequest.HttpRequestBuilder()
				.withMethod(Method.GET)
				.withPath(url)
				.withUrl(url)
				.addHeader(RequestHeader.IF_MODIFIED_SINCE.toString(), date)
				.build();
		return request;
	}
	

	
	
}
