package com.github.tobish.yaws.http.methods;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.constants.ResponseCode;
import com.github.tobish.yaws.http.constants.ResponseHeader;
import com.github.tobish.yaws.util.MimeSniffer;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

/**
 * Handle GET Requests
 */
public class HttpGetMethodHandler implements HttpMethodHandler {

	public static final Logger LOG = LoggerFactory.getLogger(HttpGetMethodHandler.class);

	private final String documentRootDir;

	public HttpGetMethodHandler(String documentRootDir) {
		super();
		this.documentRootDir = documentRootDir;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		File f = new File(documentRootDir + request.getPath());

		if (!f.exists()) {
			HttpResponse notFoundResp = new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.NOT_FOUND)
					.build();

			return notFoundResp;

		} else if (f.isDirectory()) {
			HttpResponse dirListingResponse = new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.OK)
					.addHeader(ResponseHeader.CONTENT_TYPE.toString(), "text/html")
					.withContent(buildDirectoryListing(f)).build();
			return dirListingResponse;
		} else {
			try {
				byte[] fileContent = Files.toByteArray(f);

				HttpResponse resourceResponse = new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.OK)
						.withContent(fileContent)
						.addHeader(ResponseHeader.CONTENT_TYPE.toString(), MimeSniffer.suggestMimeType(f).getMimeType())
						.build();
				return resourceResponse;

			} catch (IOException e) {
				LOG.error("Failed to read file: {}", f.getAbsolutePath());
				HttpResponse errorResponse = new HttpResponse.HttpResonseBuilder()
						.withResponseCode(ResponseCode.SERVER_ERROR).build();
				return errorResponse;
			}

		}

	}

	private byte[] buildDirectoryListing(File directory) {
		final String DIR_LISTING_TEMPLATE = "<html> " + "<head> <title>Dir-Listing</title></head>"
				+ "<body><h3>Files:</h3><hr/><p><ul>%s</ul></p></body>" + "</html>";
		List<String> fileList = Arrays.asList(directory.list());
		List<String> htmlFileList = fileList.stream().map(file -> {
			file += new File(directory, file).isDirectory() ? "/" : "";
			return "<li> <a href='./" + file + "'>" + file + "</a></li>";
		}).collect(Collectors.toList());

		String result = String.format(DIR_LISTING_TEMPLATE, Joiner.on('\n').join(htmlFileList));
		return result.getBytes();
	}

}
