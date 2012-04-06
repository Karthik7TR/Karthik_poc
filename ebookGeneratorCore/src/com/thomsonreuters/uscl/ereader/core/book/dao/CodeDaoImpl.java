/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public class CodeDaoImpl implements CodeDao {
	private SessionFactory sessionFactory;
	
	public CodeDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	/**
	 * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
	 * @param documentTypeCode
	 * @return
	 */
	@Override
	public void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode) {
		documentTypeCode = (DocumentTypeCode) sessionFactory.getCurrentSession().merge(
				documentTypeCode);
		Session session = sessionFactory.getCurrentSession();
		session.delete(documentTypeCode);
		session.flush();
	}
	
	/**
	 * Delete a JurisType Code in the Juris_TYPE_CODES table
	 * @param jurisTypeCode
	 * @return
	 */
	@Override
	public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode) {
		jurisTypeCode = (JurisTypeCode) sessionFactory.getCurrentSession().merge(
				jurisTypeCode);
		Session session = sessionFactory.getCurrentSession();
		session.delete(jurisTypeCode);
		session.flush();
	}
	
	/**
	 * Delete a KeywordType Code in the KEYWORD_TYPE_CODES table
	 * @param keywordTypeCode
	 * @return
	 */
	@Override
	public void deleteKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		Session session = sessionFactory.getCurrentSession();
		
		// Retrieve all the book definitions that use this keyword value
		@SuppressWarnings("unchecked")
		Collection<BookDefinition> books = session.createCriteria(BookDefinition.class, "book")
				.createAlias("book.keywordTypeValues", "keywordValue")
				.createAlias("keywordValue.keywordTypeCode", "keywordCode")
				.add(Restrictions.eq("keywordCode.id", keywordTypeCode.getId()))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		// Get Persistent object
		keywordTypeCode = getKeywordTypeCodeById(keywordTypeCode.getId());
		
		// Remove all KeywordTypeValue references
		Iterator<KeywordTypeValue> iter = keywordTypeCode.getValues().iterator();
		while(iter.hasNext()){
			KeywordTypeValue value = iter.next();
			
			//Remove Keyword Value from Book
			for(BookDefinition book : books) {
				book.getKeywordTypeValues().remove(value);
			}
			session.delete(value);
			iter.remove();
		}
		
		session.delete(keywordTypeCode);
		session.flush();
	}
	

	/**
	 * Delete a KeywordType Value in the KEYWORD_TYPE_VALUES table
	 * @param keywordTypeValue
	 * @return
	 */
	@Override
	public void deleteKeywordTypeValue(KeywordTypeValue keywordTypeValue) {
		Session session = sessionFactory.getCurrentSession();
		
		// Retrieve all the book definitions that use this keyword value
		@SuppressWarnings("unchecked")
		Collection<BookDefinition> books = session.createCriteria(BookDefinition.class, "book")
				.createAlias("book.keywordTypeValues", "keywordValue")
				.add(Restrictions.eq("keywordValue.id", keywordTypeValue.getId()))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		// Make keywordTypeValue persist
		keywordTypeValue = (KeywordTypeValue) session.merge(keywordTypeValue);
		KeywordTypeCode code = keywordTypeValue.getKeywordTypeCode();
		
		//Remove Keyword Value from Book
		for(BookDefinition book : books) {
			book.getKeywordTypeValues().remove(keywordTypeValue);
		}
		
		// Remove from Keyword Code collection
		code.getValues().remove(keywordTypeValue);
		session.delete(keywordTypeValue);
		session.flush();
	}
	
	/**
	 * Delete a Publisher Code in the PUBLISHER_TYPE_CODES table
	 * @param publisherCode
	 * @return
	 */
	@Override
	public void deletePublisherCode(PublisherCode publisherCode) {
		publisherCode = (PublisherCode) sessionFactory.getCurrentSession().merge(
				publisherCode);
		Session session = sessionFactory.getCurrentSession();
		session.delete(publisherCode);
		session.flush();
	}
	
	/**
	 * Delete a PubType Code in the PUB_TYPE_CODES table
	 * @param PubTypeCode
	 * @return
	 */
	@Override
	public void deletePubTypeCode(PubTypeCode pubTypeCode) {
		pubTypeCode = (PubTypeCode) sessionFactory.getCurrentSession().merge(
				pubTypeCode);
		Session session = sessionFactory.getCurrentSession();
		session.delete(pubTypeCode);
		session.flush();
	}
	
	/**
	 * Delete a State Code in the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	@Override
	public void deleteStateCode(StateCode stateCode) {
		stateCode = (StateCode) sessionFactory.getCurrentSession().merge(
				stateCode);
		Session session = sessionFactory.getCurrentSession();
		session.delete(stateCode);
		session.flush();
	}
	

	/**
	 * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
	 * @return a list of DocumentTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<DocumentTypeCode> getAllDocumentTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DocumentTypeCode.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	
	/**
	 * Get all the JurisType codes from the Juris_TYPE_CODES table
	 * @return a list of JurisTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<JurisTypeCode> getAllJurisTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JurisTypeCode.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	
	/**
	 * Get all the KeywordType codes from the KEYWORD_TYPE_CODES table
	 * @return a list of KeywordTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<KeywordTypeCode> getAllKeywordTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeywordTypeCode.class);
		List<KeywordTypeCode> codes = criteria.list();
		
		// Sort KeywordTypeCodes
		Collections.sort(codes);
		
		// Sort values in each KeywordTypeCode
		for(KeywordTypeCode code: codes) {
			List<KeywordTypeValue> values = new ArrayList<KeywordTypeValue>();
			values.addAll(code.getValues());
			Collections.sort(values);
			
			code.setValues(values);
		}
		return codes;
	}
	
	/**
	 * Get all the KeywordType codes from the KEYWORD_TYPE_VALUES table
	 * @return a list of KeywordTypeValue objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<KeywordTypeValue> getAllKeywordTypeValues() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeywordTypeValue.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	

	/**
	 * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
	 * that has keywordTypeCodeId
	 * @return a list of KeywordTypeValue objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<KeywordTypeValue> getAllKeywordTypeValues(Long keywordTypeCodeId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeywordTypeValue.class)
			.add(Restrictions.eq("keywordTypeCode.id", keywordTypeCodeId))
			.addOrder( Order.asc("name") );
		return criteria.list();
	}
	
	/**
	 * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
	 * @return a list of PublisherCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PublisherCode> getAllPublisherCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PublisherCode.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	
	/**
	 * Get all the PubType codes from the PUB_TYPE_CODES table
	 * @return a list of PubTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PubTypeCode> getAllPubTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PubTypeCode.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	
	/**
	 * Get all the State codes from the STATE_CODES table
	 * @return a list of StateCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<StateCode> getAllStateCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateCode.class)
				.addOrder( Order.asc("name") );
		return criteria.list();
	}
	

	/**
	 * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
	 * @param documentTypeCodeId
	 * @return
	 */
	@Override
	public DocumentTypeCode getDocumentTypeCodeById(Long documentTypeCodeId) {
		return (DocumentTypeCode) sessionFactory.getCurrentSession().get(DocumentTypeCode.class, documentTypeCodeId);
	}
	
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
	 * @param jurisTypeCodeId
	 * @return
	 */
	@Override
	public JurisTypeCode getJurisTypeCodeById(Long jurisTypeCodeId) {
		return (JurisTypeCode) sessionFactory.getCurrentSession().get(JurisTypeCode.class, jurisTypeCodeId);
	}
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_NAME.
	 * This search is case insensitive.
	 * @param JurisTypeCodeName
	 * @return
	 */
	@Override
	public JurisTypeCode getJurisTypeCodeByName(String jurisTypeCodeName)  {
		return (JurisTypeCode) sessionFactory.getCurrentSession().createCriteria(JurisTypeCode.class)
				.add(Restrictions.eq("name", jurisTypeCodeName).ignoreCase()).uniqueResult();
	}
		
	
	/**
	 * Get a KeywordType Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
	 * @param keywordTypeCodeId
	 * @return
	 */
	@Override
	public KeywordTypeCode getKeywordTypeCodeById(Long keywordTypeCodeId) {
		return (KeywordTypeCode) sessionFactory.getCurrentSession().get(KeywordTypeCode.class, keywordTypeCodeId);
	}
	
	/**
	 * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_NAME
	 * @param keywordTypeCodeName
	 * @return
	 */
	public KeywordTypeCode getKeywordTypeCodeByName(String keywordTypeCodeName) {
		return (KeywordTypeCode) sessionFactory.getCurrentSession().createCriteria(KeywordTypeCode.class)
				.add(Restrictions.eq("name", keywordTypeCodeName).ignoreCase()).uniqueResult();
	}
	
	/**
	 * Get a KeywordType Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
	 * @param keywordTypeValueId
	 * @return
	 */
	@Override
	public KeywordTypeValue getKeywordTypeValueById(Long keywordTypeValueId) {
		return (KeywordTypeValue) sessionFactory.getCurrentSession().get(KeywordTypeValue.class, keywordTypeValueId);
	}
	

	/**
	 * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
	 * @param publisherCodeId
	 * @return
	 */
	@Override
	public PublisherCode getPublisherCodeById(Long publisherCodeId) {
		return (PublisherCode) sessionFactory.getCurrentSession().get(PublisherCode.class, publisherCodeId);
	}

	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	@Override
	public PubTypeCode getPubTypeCodeById(Long pubTypeCodeId) {
		return (PubTypeCode) sessionFactory.getCurrentSession().get(PubTypeCode.class, pubTypeCodeId);
	}
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME.
	 * This search is case insensitive.
	 * @param pubTypeCodeName
	 * @return
	 */
	@Override
	public PubTypeCode getPubTypeCodeByName(String pubTypeCodeName)  {
		return (PubTypeCode) sessionFactory.getCurrentSession().createCriteria(PubTypeCode.class)
				.add(Restrictions.eq("name", pubTypeCodeName).ignoreCase()).uniqueResult();
	}
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
	 * @param stateCode
	 * @return
	 */
	@Override
	public StateCode getStateCodeById(Long stateCodeId) {
		return (StateCode) sessionFactory.getCurrentSession().get(StateCode.class, stateCodeId);
	}
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_NAME
	 * @param stateCodeName
	 * @return
	 */
	public StateCode getStateCodeByName(String stateCodeName){
		return (StateCode) sessionFactory.getCurrentSession().createCriteria(StateCode.class)
				.add(Restrictions.eq("name", stateCodeName).ignoreCase()).uniqueResult();
	}
	
	/**
	 * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
	 * @param documentTypeCode
	 * @return
	 */
	@Override
	public void saveDocumentTypeCode(DocumentTypeCode documentTypeCode) {
		documentTypeCode.setLastUpdated(new Date());
		
		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(documentTypeCode.getId() != null) {
			session.merge(documentTypeCode);
		} else {
			session.save(documentTypeCode);
		}
		
		session.flush();
	}
	

	/**
	 * Create or Update a JurisType Code to the Juris_TYPE_CODES table
	 * @param jurisTypeCode
	 * @return
	 */
	@Override
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode) {
		jurisTypeCode.setLastUpdated(new Date());
		
		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(jurisTypeCode.getId() != null) {
			session.merge(jurisTypeCode);
		} else {
			session.save(jurisTypeCode);
		}
		
		session.flush();
	}
	
	
	/**
	 * Create or Update a KeywordType Code to the KEYWORD_TYPE_CODES table
	 * @param keywordTypeCode
	 * @return
	 */
	@Override
	public void saveKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		keywordTypeCode.setLastUpdated(new Date());

		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(keywordTypeCode.getId() != null) {
			session.merge(keywordTypeCode);
		} else {
			session.save(keywordTypeCode);
		}
		session.flush();
	}
	
	/**
	 * Create or Update a KeywordType Value to the KEYWORD_TYPE_VALUES table
	 * @param keywordTypeValue
	 * @return
	 */
	@Override
	public void saveKeywordTypeValue(KeywordTypeValue keywordTypeValue) {
		keywordTypeValue.setLastUpdated(new Date());

		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(keywordTypeValue.getId() != null) {
			session.merge(keywordTypeValue);
		} else {
			session.save(keywordTypeValue);
		}
		session.flush();
	}
	
	/**
	 * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
	 * @param publisherCode
	 * @return
	 */
	@Override
	public void savePublisherCode(PublisherCode publisherCode) {
		publisherCode.setLastUpdated(new Date());

		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(publisherCode.getId() != null) {
			session.merge(publisherCode);
		} else {
			session.save(publisherCode);
		}
		session.flush();
	}
	
	/**
	 * Create or Update a PubType Code to the PUB_TYPE_CODES table
	 * @param PubTypeCode
	 * @return
	 */
	@Override
	public void savePubTypeCode(PubTypeCode pubTypeCode) {
		pubTypeCode.setLastUpdated(new Date());

		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(pubTypeCode.getId() != null) {
			session.merge(pubTypeCode);
		} else {
			session.save(pubTypeCode);
		}
		session.flush();
	}
	

	/**
	 * Create or Update a State Code to the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	@Override
	public void saveStateCode(StateCode stateCode) {
		stateCode.setLastUpdated(new Date());

		Session session = sessionFactory.getCurrentSession();
		
		// Determine if it a new object
		if(stateCode.getId() != null) {
			session.merge(stateCode);
		} else {
			session.save(stateCode);
		}
		session.flush();
	}
}
