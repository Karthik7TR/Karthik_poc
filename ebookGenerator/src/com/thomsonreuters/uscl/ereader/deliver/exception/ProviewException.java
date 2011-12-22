/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.exception;

/**
 * This class represents the varied types of checked exceptions that may 
 * come about when interacting with the ProView platform.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProviewException extends Exception {
	private static final long serialVersionUID = 1L;
    
	public ProviewException(String message, Throwable cause) {
        super(message, cause);
    }

	public ProviewException(String message) {
		super(message);
	}
}
