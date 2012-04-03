/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface GenerateDocumentDataBlockService {

	/**
	 * Based on passed in document guid this method retrieve corresponding collectionName using metadata service and builds documentData block. 
	 * returns as InputStream. 
	 * @param titleId
	 * @param jobInstanceId
	 * @param docGuid
	 * @return
	 * @throws EBookFormatException
	 */
	public InputStream getDocumentDataBlockAsStream(String titleId, int jobInstanceId,
			String docGuid)throws EBookFormatException;
}
