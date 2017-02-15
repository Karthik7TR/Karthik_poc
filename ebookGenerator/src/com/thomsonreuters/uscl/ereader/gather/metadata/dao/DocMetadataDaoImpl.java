package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage DocMetadata entities.
 *
 */

public class DocMetadataDaoImpl implements DocMetadataDao
{
    private SessionFactory sessionFactory;

    public DocMetadataDaoImpl(final SessionFactory hibernateSessionFactory)
    {
        sessionFactory = hibernateSessionFactory;
    }

    /**
     * Used to determine whether or not to merge the entity or persist the
     * entity when calling Store
     *
     * @see store
     *
     *
     */
    public boolean canBeMerged(final DocMetadata entity)
    {
        return true;
    }

    /**
     * Query - findDocMetadataMapByDocUuid
     *
     * @param docUuid
     *            from the document
     * @returns a map of the document family guids associated with the document
     *          uuid
     *
     *
     */
    @Override
    @Transactional
    public Map<String, String> findDocMetadataMapByDocUuid(final String docUuid) throws DataAccessException
    {
        final Map<String, String> mp = new HashMap<>();

        final Query query = createNamedQuery("findDocMetadataMapByDocUuid");
        query.setString("doc_uuid", docUuid);

        final List<String> docFamilyGuidList = query.list();

        for (int i = 0; i < docFamilyGuidList.size(); i++)
        {
            mp.put(docFamilyGuidList.get(i), docUuid);
        }

        return mp;
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public Query createNamedQuery(final String queryName)
    {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        return query;
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public DocMetadata persist(final DocMetadata toPersist)
    {
        sessionFactory.getCurrentSession().save(toPersist);
        flush();
        return toPersist;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void remove(DocMetadata toRemove)
    {
        toRemove = (DocMetadata) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(toRemove);
        flush();
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public void update(final DocMetadata toUpdate)
    {
        sessionFactory.getCurrentSession().update(toUpdate);
        flush();
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public void flush()
    {
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public DocMetadata findDocMetadataByPrimaryKey(final DocMetadataPK pk)
    {
        final Session session = sessionFactory.getCurrentSession();
        return (DocMetadata) session.get(DocMetadata.class, pk);
    }

    @Override
    public Map<String, String> findDistinctFamilyGuidsByJobId(final Long jobInstanceId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final List<Object[]> docMetaList = session.createCriteria(DocMetadata.class)
            .setProjection(
                Projections.distinct(
                    (Projections.projectionList()
                        .add(Projections.property("docUuid"))
                        .add(Projections.property("docFamilyUuid")))))
            .add(Restrictions.eq("jobInstanceId", jobInstanceId))
            .list();
//		List<DocMetadata> docMetaList = session.createCriteria(DocMetadata.class)
//	    .add( Restrictions.eq("jobInstanceId", instanceJobId))
//	    .list();

        final Map<String, String> docMap = new HashMap<>();

        for (final Object[] arr : docMetaList)
        {
            if (arr[1] != null) // Xena content has no docFamilyGuid
            {
                docMap.put(arr[0].toString(), arr[1].toString());
            }
        }
        return docMap;
    }

    @Override
    public List<String> findDistinctSplitTitlesByJobId(final Long jobInstanceId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final List<String> docMetaList = session.createCriteria(DocMetadata.class)
            .setProjection(
                Projections.distinct((Projections.projectionList().add(Projections.property("splitBookTitleId")))))
            .add(Restrictions.eq("jobInstanceId", jobInstanceId))
            .addOrder(Order.asc("splitBookTitleId"))
            .list();

        final List<String> splitTitleIdList = new ArrayList<>();

        if (docMetaList.size() > 0)
        {
            splitTitleIdList.addAll(docMetaList);
        }

        return splitTitleIdList;
    }

    @Override
    @Transactional
    public void saveMetadata(final DocMetadata metadata)
    {
        final Session session = sessionFactory.getCurrentSession();
        session.save(metadata);
        session.flush();
    }

    @Override
    @Transactional
    public void updateMetadata(final DocMetadata metadata)
    {
        final Session session = sessionFactory.getCurrentSession();
        session.update(metadata);
        session.flush();
    }

    @Override
    public DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(final Long jobInstanceId)
    {
        final Session session = sessionFactory.getCurrentSession();

        // Using LinkedHashSet to preserve insertion order based on what is returned from DB
        final Set<DocMetadata> documentMetadataSet = new LinkedHashSet<>();

        final List<DocMetadata> docMetaList = session.createCriteria(DocMetadata.class)
            .add(Restrictions.eq("jobInstanceId", jobInstanceId))
            .addOrder(Order.asc("docUuid"))
            .list();

        documentMetadataSet.addAll(docMetaList);
        final DocumentMetadataAuthority documentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);
        return documentMetadataAuthority;
    }

    /**
     * Query - findDocMetadataMapByPartialCiteMatchAndJobId
     *
     * @param jobInstanceId
     *            jobinstanceId from the run
     * @param cite
     *            from the document
     * @returns a documentMetadata
     *
     *
     */
    @Override
    @Transactional
    public DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(final Long jobInstanceId, final String cite)
        throws DataAccessException
    {
        final Query query = createNamedQuery("findDocumentMetaDataByCiteAndJobId");
        query.setParameter("jobInstaneId", jobInstanceId);
        query.setParameter("normalizedCite", "%" + cite);

        final List<DocMetadata> docMetaDataList = query.list();

        if (docMetaDataList.size() > 0)
        {
            return docMetaDataList.get(0);
        }
        else
        {
            return null;
        }
    }
}
