package com.github.tobish.yaws.httpmethods;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.ResponseHeader;
import com.github.tobish.yaws.http.constants.ResponseCode;
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
			HttpResponse notFoundResp = 
					new HttpResponse.HttpResonseBuilder()
					.withResponseCode(ResponseCode.NOT_FOUND)
					.build();
			
			return notFoundResp;
			
		} else if (f.isDirectory()) {
			HttpResponse dirListingResponse = new HttpResponse.HttpResonseBuilder()
					.withResponseCode(ResponseCode.OK)
					.addHeader(ResponseHeader.CONTENT_TYPE.toString(), "text/html")
					.withContent( Joiner.on(",").join(f.list()).getBytes() )
					.build();
			return dirListingResponse;
		} else {
			try {
				byte[] fileContent = Files.toByteArray(f);
				
				HttpResponse resourceResponse = 
						new HttpResponse.HttpResonseBuilder()
						.withResponseCode(ResponseCode.OK)
						.withContent(fileContent)
						.build();
				return resourceResponse;
				
			} catch (IOException e) {
				LOG.error("Failed to read file: {}" , f.getAbsolutePath());
				HttpResponse errorResponse = 
						new HttpResponse.HttpResonseBuilder()
						.withResponseCode(ResponseCode.SERVER_ERROR)
						.build();
				return errorResponse;
			}

			
		}
		
		
	}

}
