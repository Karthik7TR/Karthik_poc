/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.format.dao.XSLTMapperDao;
import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * Spring service that handles requests to retrieve XSLTMapperEntity
 * @author Ripu Jain U0115290
 */
@Service
public class XSLTMapperServiceImpl implements XSLTMapperService {

	private XSLTMapperDao xsltMapperDao;
	
	/**
	 * Return XSLT style sheet name for a given document collection and doc-type.
	 * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
	 * @return XSLT style sheet name.
	 */
	@Override
	@Transactional(readOnly = true)
	public String getXSLT(String collection, String docType) {
		if (xsltMapperDao == null) {
			throw new IllegalArgumentException("xsltMapperDao was not injected " +
					"into XSLTMapperService! This is a programming error. " +
					"Check the Spring configuration!");
		}
		XSLTMapperEntity xslt = xsltMapperDao.getXSLT(collection, docType);
		if (xslt == null) return null;
		return xslt.getXSLT();
	}

	public void setXsltMapperDao(XSLTMapperDao xsltMapperDao) {
		this.xsltMapperDao = xsltMapperDao;		
	}
}
