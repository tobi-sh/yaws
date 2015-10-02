package com.github.tobish.yaws.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tobish.yaws.http.constants.MimeType;
import com.google.common.io.Files;

/*
 * Suggest a mime type of a given file 
 */
public class MimeSniffer {
	
	public static final Logger LOG = LoggerFactory.getLogger(MimeSniffer.class);
	
	/**
	 * Suggest the mime type of a given file 
	 * 
	 * @param file
	 * @return
	 */
	public static MimeType suggestMimeType(File file) {
		String fileExtension = Files.getFileExtension(file.getAbsolutePath());
		try {
			return MimeType.valueOf(fileExtension.toUpperCase());	
		}
		catch (Throwable e) {
			LOG.error("Unable to set the content-type for: {} (file extention is unknown)", file.getName());
			return MimeType.UNKNOWN;
		}
		 
	}

}
