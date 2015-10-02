package com.github.tobish.yaws.util;

import java.io.File;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.github.tobish.yaws.http.constants.MimeType;

public class MimeSnifferTest {
	
	@Test
	public void testRecognizeHtmlFile() {
		MimeType mimeType = MimeSniffer.suggestMimeType(new File("/tmp/pizza.html"));
		Assert.assertThat(mimeType, Matchers.is(MimeType.HTML));
	}
	
	@Test
	public void testUnknownFileExtention() {
		MimeType mimeType = MimeSniffer.suggestMimeType(new File("/tmp/pizza.betterHtml"));
		Assert.assertThat(mimeType, Matchers.is(MimeType.UNKNOWN));
	}
	
}
