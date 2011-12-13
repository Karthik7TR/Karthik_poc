/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader;

/**
 * This class represents the names of well-known business keys used within the Spring Batch JobExecutionContext.
 * 
 * <p>The intent is that developers of steps specify the properties that they depend upon in this class, and 
 * use these names to retrieve those property values from the Spring Batch Job Execution Context.</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class JobExecutionKey {
	
	/**
	 * The path from which to assemble the eBook from.
	 */
	public static final String EBOOK_DIRECTORY_PATH = "eBookDirectoryPath";
	
	/**
	 * The path to the assembled eBook on NAS.
	 */
	public static final String EBOOK_FILE_PATH = "eBookFilePath";
}
