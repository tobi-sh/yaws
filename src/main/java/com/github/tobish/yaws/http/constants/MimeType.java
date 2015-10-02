package com.github.tobish.yaws.http.constants;


/**
 * Very basic list of some common mime types
 *
 */
public enum MimeType {
	
	HTM("text/html"),
	HTML("text/html"),
	
	TXT("text/plain"),
	CSS("text/css"),
	
	PDF("application/pdf"),
	JS("application/javascript"),
	JSON("application/json"),
	
	PNG("image/png"),
	SVG("image/svg+xml"),
	BMP("image/bmp"),
	GIF("image/gif"),
	IEF("image/ief"),
	JPEG("image/jpeg"),
	JPG("image/jpeg"),
	TIFF("image/tiff"),
	
	UNKNOWN("");
	
	private final String mimeType;
	
	private MimeType(String value) {
		this.mimeType = value;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
}