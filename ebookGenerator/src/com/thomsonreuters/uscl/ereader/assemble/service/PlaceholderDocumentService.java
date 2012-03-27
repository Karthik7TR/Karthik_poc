/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.OutputStream;

/**
 * Implementors of PlaceholderDocumentService are responsible for generating placeholder documents.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public interface PlaceholderDocumentService {
	public void generatePlaceholderDocument(OutputStream documentStream, String displayText) throws PlaceholderDocumentServiceException;
}
