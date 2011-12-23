/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang.StringUtils;

import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * XSLTMapperDaoImpl is a DAO to retrieve the XSLTMapperEntity.
 * 
 * @author Ripu Jain U0115290 
 */
@Repository("xsltMapperDao")
public class XSLTMapperDaoImpl implements XSLTMapperDao{

	@Autowired
	private SessionFactory xsltSessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
        this.xsltSessionFactory = sessionFactory;
    }
	
	/**
     * Retrieves the XSLT style sheet for a given collection and doc-type.
     *
     * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
     * @return XSLTMapperEntity the XSLT entity object.
     */
	@Override
	public XSLTMapperEntity getXSLT(String collection, String docType) {
		if (StringUtils.isBlank(collection))
			throw new IllegalArgumentException("Failed to builed the query to retrieve XSLT. " +
					"Collection name can not be null.");
		
		Query query;
		if (StringUtils.isNotBlank(docType)) {
			query = xsltSessionFactory.getCurrentSession().getNamedQuery("getXSLT");
			query.setString("collection", collection);
			query.setString("doc_type", docType);
			
		}
		else {
			query = xsltSessionFactory.getCurrentSession().getNamedQuery("getXSLTWhereDocTypeIsNull");
			query.setString("collection", collection);
		}
		Object queryResult = query.uniqueResult();
		if (queryResult == null) return null;
		return (XSLTMapperEntity) queryResult;
	}
}
