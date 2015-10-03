package com.github.tobish.yaws.util;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class Md5EtagProviderTest {

	@Test
	public void testCalculationOfMd5Etag() {
		Md5EtagProvider etagProvider = new Md5EtagProvider();
		String etag = etagProvider.provideEtag("Hello".getBytes());
		
		assertThat(etag, is("\"8b1a9953c4611296a827abf8c47804d7\""));
	}
	
	@Test
	public void testInvalidInputShouldReturnAnEmptyString() {
		Md5EtagProvider etagProvider = new Md5EtagProvider();
		String etag = etagProvider.provideEtag(null);
		
		assertThat(etag, isEmptyString());
	}
	
	
}
