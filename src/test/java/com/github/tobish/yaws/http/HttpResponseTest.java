package com.github.tobish.yaws.http;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.github.tobish.yaws.http.constants.ResponseCode;

public class HttpResponseTest {

	@Test
	public void testStringRepresentationOfResponse() {
		HttpResponse response = new HttpResponse.HttpResonseBuilder().
				withResponseCode(ResponseCode.METHOD_NOT_ALLOWED).build();
		
		Assert.assertThat(response.toString(), Matchers.containsString("HTTP/1.1 405 Method Not Allowed" + System.lineSeparator()));
	}
	
}
