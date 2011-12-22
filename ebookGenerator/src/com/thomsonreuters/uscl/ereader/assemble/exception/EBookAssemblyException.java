/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.exception;

/**
 * A subclass of Exception intended to be thrown by EbookAssemblyService in cases where low-level file-system resources weren't available, or when the archive itself is corrupt.
 * @author u0081674
 *
 */
public class EBookAssemblyException extends Exception {
	private static final long serialVersionUID = 1L;
	
    public EBookAssemblyException(String message, Throwable cause) {
        super(message, cause);
    }
}
