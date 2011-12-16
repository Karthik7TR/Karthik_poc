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

import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * XSLTMapperDaoImpl is a DAO to retrieve the XSLTMapperEntity.
 * 
 * @author Ripu Jain U0115290 
 */
@Repository("XSLTMapperDao")
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
		Query query = xsltSessionFactory.getCurrentSession().getNamedQuery("getXSLT");
		query.setString("collection", collection);
		query.setString("doc_type", docType);
		return (XSLTMapperEntity) query.uniqueResult();
	}
}
