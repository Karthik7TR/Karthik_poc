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
	/**
	 * Writes a "placeholder" XHTML document containing the specified display text to the provided {@link OutputStream}.
	 * 
	 * <p>We do this to support rendering text in proview where there is no corresponding document text loaded to Novus.</p>
	 * 
	 * @param documentStream the {@link} to write the document to.
	 * @param displayText the text to inject into the title and body of the document.
	 * @throws PlaceholderDocumentServiceException if the data could not be written or the document template was misconfigured.
	 */
	public void generatePlaceholderDocument(OutputStream documentStream, String displayText) throws PlaceholderDocumentServiceException;
	
	/**
	 * Writes a "placeholder" XHTML document containing the specified display text to the provided {@link OutputStream}.
	 * 
	 * <p>We do this to support rendering text in proview where there is no corresponding document text loaded to Novus.</p>
	 * 
	 * @param documentStream the {@link} to write the document to.
	 * @param displayText the text to inject into the title and body of the document.
	 * @param anchorName the anchor to embed as the first tag within the body of the XHTML document.
	 * @throws PlaceholderDocumentServiceException if the data could not be written or the document template was misconfigured.
	 */
	public void generatePlaceholderDocument(OutputStream documentStream, String displayText, String anchorName) throws PlaceholderDocumentServiceException;
}
