/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.service;

/**
 * Spring service that handles requests to retrieve XSLTMapperEntity
 * @author Ripu Jain U0115290
 */
public interface XSLTMapperService {
	/**
	 * Return XSLT style sheet name for a given document collection and doc-type.
	 * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
	 * @return XSLT style sheet name.
	 */
	public String getXSLT(String collection, String docType);
}
