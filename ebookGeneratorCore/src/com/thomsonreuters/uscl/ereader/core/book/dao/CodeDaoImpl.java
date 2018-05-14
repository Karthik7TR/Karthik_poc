package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CodeDaoImpl implements CodeDao {
    private SessionFactory sessionFactory;

    public CodeDaoImpl(final SessionFactory sessFactory) {
        sessionFactory = sessFactory;
    }

    /**
     * Delete a PubType Code in the PUB_TYPE_CODES table
     * @param PubTypeCode
     * @return
     */
    @Override
    public void deletePubTypeCode(PubTypeCode pubTypeCode) {
        pubTypeCode = (PubTypeCode) sessionFactory.getCurrentSession().merge(pubTypeCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(pubTypeCode);
        session.flush();
    }

    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of PubTypeCode objects
     */
    @Override
    public List<PubTypeCode> getAllPubTypeCodes() {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(PubTypeCode.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    @Override
    public PubTypeCode getPubTypeCodeById(final Long pubTypeCodeId) {
        return (PubTypeCode) sessionFactory.getCurrentSession().get(PubTypeCode.class, pubTypeCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME.
     * This search is case insensitive.
     * @param pubTypeCodeName
     * @return
     */
    @Override
    public PubTypeCode getPubTypeCodeByName(final String pubTypeCodeName) {
        return (PubTypeCode) sessionFactory.getCurrentSession()
            .createCriteria(PubTypeCode.class)
            .add(Restrictions.eq("name", pubTypeCodeName).ignoreCase())
            .uniqueResult();
    }

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param PubTypeCode
     * @return
     */
    @Override
    public void savePubTypeCode(final PubTypeCode pubTypeCode) {
        pubTypeCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (pubTypeCode.getId() != null) {
            session.merge(pubTypeCode);
        } else {
            session.save(pubTypeCode);
        }
        session.flush();
    }
}
