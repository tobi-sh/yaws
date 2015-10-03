package com.github.tobish.yaws.util;

/**
 * Provide an etag for a given byte array. This etag must be well-formed. If the calculation of the etag fails return an empty string
 * 
 */
public interface EtagProvider {
	
	/**
	 * Get an etag for the given content
	 * 
	 * @param content - the content to be signed
	 * @return a well formed etag or an empty string if calculation fails
	 */
	String provideEtag(byte[] content);
}
