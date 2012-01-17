/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.exception;

/**
 * Generic gather exceptions that is thrown when I/O issues or other formatting anomalies have been encountered.
 * 
 * @author u0072938
 */
public class EBookGatherException extends Exception
{
	private static final long serialVersionUID = 1L;

	public EBookGatherException(String message) {
        super(message);
    }
	
	public EBookGatherException(String message, Throwable cause) {
        super(message, cause);
    }
}
