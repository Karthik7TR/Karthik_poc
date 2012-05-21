/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.util;

/**
 * A subclass of Exception intended to be thrown by Ebook Server access Service in cases if it fails to connect to server with given credentials .
 *    
 *  @author Mahendra Survase (u0105927)
 */
public class EBookServerException extends Exception {
	private static final long serialVersionUID = 1L;

	public EBookServerException(String message) {
        super(message);
    }
	
    public EBookServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
