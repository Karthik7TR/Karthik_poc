/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public class CodeDaoImpl implements CodeDao {
	private SessionFactory sessionFactory;
	
	public CodeDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	/**
	 * Get all the State codes from the STATE_CODES table
	 * @return a list of StateCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<StateCode> getAllStateCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateCode.class);
		return criteria.list();
	}
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
	 * @param stateCode
	 * @return
	 */
	@Override
	public StateCode getStateCode(Long stateCodeId) {
		return (StateCode) sessionFactory.getCurrentSession().get(StateCode.class, stateCodeId);
	}
	
	/**
	 * Create or Update a State Code to the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	@Override
	public void saveStateCode(StateCode stateCode) {
		Session session = sessionFactory.getCurrentSession();
		session.save(stateCode);
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
	 * Get all the PubType codes from the PUB_TYPE_CODES table
	 * @return a list of PubTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PubTypeCode> getAllPubTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PubTypeCode.class);
		return criteria.list();
	}
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	@Override
	public PubTypeCode getPubTypeCode(Long pubTypeCodeId) {
		return (PubTypeCode) sessionFactory.getCurrentSession().get(PubTypeCode.class, pubTypeCodeId);
	}
	
	/**
	 * Create or Update a PubType Code to the PUB_TYPE_CODES table
	 * @param PubTypeCode
	 * @return
	 */
	@Override
	public void savePubTypeCode(PubTypeCode pubTypeCode) {
		Session session = sessionFactory.getCurrentSession();
		session.save(pubTypeCode);
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
	 * Get all the JurisType codes from the Juris_TYPE_CODES table
	 * @return a list of JurisTypeCode objects
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<JurisTypeCode> getAllJurisTypeCodes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JurisTypeCode.class);
		return criteria.list();
	}
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
	 * @param jurisTypeCodeId
	 * @return
	 */
	@Override
	public JurisTypeCode getJurisTypeCode(Long jurisTypeCodeId) {
		return (JurisTypeCode) sessionFactory.getCurrentSession().get(JurisTypeCode.class, jurisTypeCodeId);
	}
	
	/**
	 * Create or Update a JurisType Code to the Juris_TYPE_CODES table
	 * @param jurisTypeCode
	 * @return
	 */
	@Override
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode) {
		Session session = sessionFactory.getCurrentSession();
		session.save(jurisTypeCode);
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

}
