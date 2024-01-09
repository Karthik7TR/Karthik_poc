package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class PaceMetadataDaoImpl implements PaceMetadataDao {
    private SessionFactory sessionFactory;

    public PaceMetadataDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public PaceMetadata findPaceMetadataByPrimaryKey(final Long publicationId) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        return (PaceMetadata) session.get(PaceMetadata.class, publicationId);
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public List<PaceMetadata> findPaceMetadataByPubCode(final Long pubCode) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        final List<PaceMetadata> paceMetadata =
            session.createCriteria(PaceMetadata.class).add(Restrictions.eq("publicationCode", pubCode)).list();

        return paceMetadata;
    }

    @Transactional
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void remove(PaceMetadata toRemove) throws DataAccessException {
        toRemove = (PaceMetadata) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(toRemove);
        flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void saveMetadata(final PaceMetadata metadata) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(metadata);
        session.flush();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void updateMetadata(final PaceMetadata metadata) {
        sessionFactory.getCurrentSession().update(metadata);
        flush();
    }
}
