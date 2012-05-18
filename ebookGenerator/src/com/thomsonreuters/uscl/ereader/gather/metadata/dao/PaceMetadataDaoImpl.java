/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;


/**
 * 
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class PaceMetadataDaoImpl implements PaceMetadataDao
{
    private SessionFactory sessionFactory;

    public PaceMetadataDaoImpl(SessionFactory hibernateSessionFactory)
    {
        this.sessionFactory = hibernateSessionFactory;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public PaceMetadata findPaceMetadataByPrimaryKey(Long publicationId)
        throws DataAccessException
    {
    	Session session = sessionFactory.getCurrentSession();
		return (PaceMetadata) session.get(PaceMetadata.class, publicationId);
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PaceMetadata> findPaceMetadataByPubCode(Long pubCode)
        throws DataAccessException
    {
        Session session = sessionFactory.getCurrentSession();
        List<PaceMetadata> paceMetadata =
            session.createCriteria(PaceMetadata.class)
                   .add(Restrictions.eq("publicationCode", pubCode)).list();

        return paceMetadata;
    }

    @Transactional
    public void flush()
    {
        sessionFactory.getCurrentSession().flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void remove(PaceMetadata toRemove) throws DataAccessException
    {
        toRemove = (PaceMetadata) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(toRemove);
        flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void saveMetadata(PaceMetadata metadata)
    {
        Session session = sessionFactory.getCurrentSession();
        session.save(metadata);
        session.flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void updateMetadata(PaceMetadata metadata)
    {
        sessionFactory.getCurrentSession().update(metadata);
        flush();
    }
}
