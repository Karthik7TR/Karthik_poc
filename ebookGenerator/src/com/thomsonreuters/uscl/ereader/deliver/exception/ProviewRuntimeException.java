/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.exception;

/**
 * Thrown when error responses are received from the ProviewClient.
 *  
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class ProviewRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ProviewRuntimeException(String message) {
		super(message);
	}
	
	public ProviewRuntimeException(String message, Throwable cause){
		super(message, cause);
	}
}
