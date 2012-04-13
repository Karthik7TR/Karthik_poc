/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.frontmatter.exception;

/**
 * Generic Front Matter Generation exceptions that is thrown when 
 * any HTML generation anomalies are encountered.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class EBookFrontMatterGenerationException extends Exception
{
	private static final long serialVersionUID = 1L;

	public EBookFrontMatterGenerationException(String message) {
        super(message);
    }
	
	public EBookFrontMatterGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
