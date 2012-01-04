/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.exception;

/**
 * Generic Format exceptions that is thrown when I/O issues or other formatting anomalies have been encountered.
 * 
 * @author u0095869
 */
public class EBookFormatException extends Exception
{
	private static final long serialVersionUID = 1L;

	public EBookFormatException(String message) {
        super(message);
    }
	
	public EBookFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
