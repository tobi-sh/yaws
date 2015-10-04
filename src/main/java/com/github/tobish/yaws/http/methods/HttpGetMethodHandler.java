package com.github.tobish.yaws.http.methods;

import static com.github.tobish.yaws.http.constants.RequestHeader.IF_MATCH;
import static com.github.tobish.yaws.http.constants.RequestHeader.IF_MODIFIED_SINCE;
import static com.github.tobish.yaws.http.constants.ResponseHeader.CONTENT_TYPE;
import static com.github.tobish.yaws.http.constants.ResponseHeader.ETAG;
import static com.github.tobish.yaws.http.constants.ResponseHeader.LAST_MODIFIED;
import static com.github.tobish.yaws.util.MimeSniffer.suggestMimeType;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.http.HttpRequest;
import com.github.tobish.yaws.http.HttpResponse;
import com.github.tobish.yaws.http.HttpResponse.HttpResonseBuilder;
import com.github.tobish.yaws.http.constants.MimeType;
import com.github.tobish.yaws.http.constants.RequestHeader;
import com.github.tobish.yaws.http.constants.ResponseCode;
import com.github.tobish.yaws.util.EtagProvider;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

/**
 * Handle GET Requests
 */
public class HttpGetMethodHandler implements HttpMethodHandler {

	public static final Logger LOG = LoggerFactory.getLogger(HttpGetMethodHandler.class);

	private final String documentRootDir;

	private final EtagProvider etagProvider;

	public HttpGetMethodHandler(String documentRootDir, EtagProvider etagProvider) {
		super();
		this.documentRootDir = documentRootDir;
		this.etagProvider = etagProvider;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		File f = new File(documentRootDir + request.getPath());

		if (!f.exists()) { // handle file not found scenario
			HttpResponse notFoundResp = new HttpResponse.HttpResonseBuilder().withResponseCode(ResponseCode.NOT_FOUND)
					.build();

			return notFoundResp;
		}

		try {
			byte[] fileContent = f.isDirectory() ? buildDirectoryListing(f) : Files.toByteArray(f);

			String etag = etagProvider.provideEtag(fileContent);
			Date fileModifyDate = new Date(f.lastModified());

			HttpResonseBuilder responseBuilder = createBuilderWithCommonHeader(f, etag, fileModifyDate);

			if (missedPreconditions(request, etag)) {
				responseBuilder.withResponseCode(ResponseCode.PRECONDITION_FAILED);
			} else if (isCachedEntity(request, etag, fileModifyDate)) {
				responseBuilder.withResponseCode(ResponseCode.NOT_MODIFIED);
			} else {
				responseBuilder.withResponseCode(ResponseCode.OK).withContent(fileContent);
			}

			return responseBuilder.build();

		} catch (IOException e) {
			LOG.error("Failed to read file: {}", f.getAbsolutePath());
			HttpResponse errorResponse = new HttpResponse.HttpResonseBuilder()
					.withResponseCode(ResponseCode.SERVER_ERROR).build();
			return errorResponse;
		}

	}

	private boolean missedPreconditions(HttpRequest request, String etag) {
		List<String> ifMatchHeader = request.getHeader().getOrDefault(IF_MATCH.toString(), Collections.EMPTY_LIST);
		return !ifMatchHeader.isEmpty() && !ifMatchHeader.contains("*") && !ifMatchHeader.contains(etag);
	}

	private HttpResonseBuilder createBuilderWithCommonHeader(File f, String etag, Date fileModifyDate) {
		String fileModifiedIsoString = LocalDateTime
				.ofInstant(fileModifyDate.toInstant(), ZoneOffset.ofHours(fileModifyDate.getTimezoneOffset() / 60))
				.format(ISO_DATE_TIME);
		HttpResonseBuilder responseBuilder = new HttpResponse.HttpResonseBuilder()
				.addHeader(CONTENT_TYPE.toString(),
						f.isDirectory() ? MimeType.HTML.getMimeType() : suggestMimeType(f).getMimeType())
				.addHeader(ETAG.toString(), etag).addHeader(LAST_MODIFIED.toString(), fileModifiedIsoString);
		return responseBuilder;
	}

	private boolean isCachedEntity(HttpRequest request, String etag, Date fileModifyDate) {
		return etagsMatches(request, etag) || resourceWasNotModifedSince(request, fileModifyDate);
	}

	private boolean resourceWasNotModifedSince(HttpRequest request, Date fileModifyDate) {
		List<String> ifModifiedSinceHeaders = request.getHeader().getOrDefault(IF_MODIFIED_SINCE.toString(),
				Collections.EMPTY_LIST);
		if (ifModifiedSinceHeaders.size() != 1) {
			return false;
		}
		String ifModifedSinceHeader = ifModifiedSinceHeaders.get(0);
		Date ifModifedSinceDate = new Date(
				LocalDateTime.parse(ifModifedSinceHeader, ISO_DATE_TIME).toEpochSecond(ZoneOffset.ofHours(1)));

		boolean modifedBefore = fileModifyDate.before(ifModifedSinceDate);

		return modifedBefore;
	}

	private boolean etagsMatches(HttpRequest request, String etag) {
		List<String> reqEtags = request.getHeader().getOrDefault(RequestHeader.IF_NONE_MATCH.toString(),
				Collections.EMPTY_LIST);
		boolean etagMatch = reqEtags.contains(etag);
		return etagMatch;
	}

	private byte[] buildDirectoryListing(File directory) {
		final String DIR_LISTING_TEMPLATE = "<html> " + "<head> <title>Dir-Listing</title></head>"
				+ "<body><h3>Files:</h3><hr/><p><ul>%s</ul></p></body>" + "</html>";
		List<String> fileList = Arrays.asList(directory.list());
		List<String> htmlFileList = fileList.stream().map(file -> {
			file += new File(directory, file).isDirectory() ? "/" : "";
			return "<li> <a href='./" + file + "'>" + file + "</a></li>";
		}).collect(Collectors.toList());

		String parentPath = "<li> <a href='" + ".." + "'>" + ".." + "</a></li>";
		htmlFileList.add(0, parentPath);

		String result = String.format(DIR_LISTING_TEMPLATE, Joiner.on('\n').join(htmlFileList));
		return result.getBytes();
	}

}
