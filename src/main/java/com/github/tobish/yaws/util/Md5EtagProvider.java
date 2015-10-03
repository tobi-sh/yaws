package com.github.tobish.yaws.util;

import java.math.BigInteger;
import java.security.MessageDigest;


/**
 * Return the MD5Sum of the given content as a strong etag
 *
 */
public class Md5EtagProvider implements EtagProvider {
	
	@Override
	public String provideEtag(byte[] content) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			String etag = "\"" + new BigInteger(1,md.digest(content)).toString(16) + "\"";
			return etag;
		} catch (Throwable t) {
			return "";	
		}
		
	}

}
