package com.thomsonreuters.uscl.ereader.stats.dao;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.lt;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

public class PublishingStatsDaoImpl implements PublishingStatsDao
{
    private static final String EBOOK_DEF_ID = "ebookDefId";
    private static final String PUBLISH_STATUS = "publishStatus";
    private static final String AUDIT = "audit";
    private static final String JOB_INSTANCE_ID = "jobInstanceId";

    private SessionFactory sessionFactory;

    public PublishingStatsDaoImpl(final SessionFactory hibernateSessionFactory)
    {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public Date getSysDate()
    {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery("getSysdate");
        return (Date) query.uniqueResult();
    }

    @Override
    public List<PublishingStats> findAllPublishingStats()
    {
        return sessionFactory.getCurrentSession().createCriteria(PublishingStats.class).list();
    }

    @Override
    public PublishingStats findJobStatsByJobId(final Long jobId)
    {
        return (PublishingStats) sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .setFetchMode(AUDIT, FetchMode.JOIN)
            .add(Restrictions.eq(JOB_INSTANCE_ID, jobId))
            .uniqueResult();
    }

    @Override
    public void saveJobStats(final PublishingStats jobstats)
    {
        final Session session = sessionFactory.getCurrentSession();
        session.save(jobstats);
        session.flush();
    }

    @Override
    public void deleteJobStats(final PublishingStats toRemove)
    {
        final Session session = sessionFactory.getCurrentSession();
        final PublishingStats merged = (PublishingStats) session.merge(toRemove);
        session.delete(merged);
        session.flush();
    }

    @Override
    public EbookAudit findAuditInfoByJobId(final Long jobId)
    {
        final PublishingStats stats = (PublishingStats) sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .add(Restrictions.eq(JOB_INSTANCE_ID, jobId))
            .uniqueResult();

        EbookAudit ebookAudit = null;
        if (stats != null)
        {
            ebookAudit = stats.getAudit();
            Hibernate.initialize(ebookAudit);
        }
        return ebookAudit;
    }

    /**
     * Find Publishing stats for ebook
     *
     * @param eBookDefId
     * @return
     */
    @Override
    public List<PublishingStats> findPublishingStatsByEbookDef(final Long eBookDefId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias(AUDIT, "book")
            .setFetchMode(AUDIT, FetchMode.JOIN)
            .add(Restrictions.eq(EBOOK_DEF_ID, eBookDefId));

        return criteria.list();
    }

    @Override
    public List<String> findSuccessfullyPublishedIsbnByTitleId(final String titleId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias(AUDIT, "book")
            .setFetchMode(AUDIT, FetchMode.JOIN)
            .add(Restrictions.eq("book.titleId", titleId))
            .add(getSuccessfulPublishStatusRestriction())
            .setProjection(Projections.distinct(Projections.property("book.isbn")));
        return criteria.list();
    }

    @Override
    public Long findSuccessfullyPublishedGroupBook(final Long ebookDefId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias(AUDIT, "book")
            .setFetchMode(AUDIT, FetchMode.JOIN)
            .add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
            .add(getSuccessfulPublishStatusRestriction())
            .add(Restrictions.isNotNull("book.groupName"))
            .setProjection(Projections.distinct(Projections.property("book.ebookDefinitionId")));
        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter, final PublishingStatsSort sort)
    {
        final Criteria criteria = addFilters(filter);

        final String orderByColumn = sort.getOrderByColumnName();
        if (sort.isAscending())
        {
            criteria.addOrder(Order.asc(orderByColumn));
        }
        else
        {
            criteria.addOrder(Order.desc(orderByColumn));
        }

        final int itemsPerPage = sort.getItemsPerPage();
        criteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage));
        criteria.setMaxResults(itemsPerPage);

        return criteria.list();
    }

    @Override
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter)
    {
        final Criteria criteria = addFilters(filter);

        return criteria.list();
    }

    @Override
    public int numberOfPublishingStats(final PublishingStatsFilter filter)
    {
        final Criteria criteria = addFilters(filter);

        final Number rows = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();

        return rows.intValue();
    }

    @Override
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobId)
    {
        final Session session = sessionFactory.getCurrentSession();
        final DetachedCriteria subQuery = DetachedCriteria.forClass(PublishingStats.class)
            .add(eq(JOB_INSTANCE_ID, jobId))
            .setProjection(Projections.property(EBOOK_DEF_ID));
        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .add(Property.forName(EBOOK_DEF_ID).eq(subQuery))
            .add(getSuccessfulPublishStatusRestriction())
            //jobInstanceId of prev job instances are less then current one
            .add(lt(JOB_INSTANCE_ID, jobId))
            .addOrder(Order.desc(JOB_INSTANCE_ID))
            .setMaxResults(1);
        return (PublishingStats) criteria.uniqueResult();
    }

    private Criteria addFilters(final PublishingStatsFilter filter)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias(AUDIT, "book")
            .setFetchMode(AUDIT, FetchMode.JOIN);

        if (filter.getFrom() != null)
        {
            criteria.add(Restrictions.ge("jobSubmitTimestamp", filter.getFrom()));
        }
        if (filter.getTo() != null)
        {
            criteria.add(Restrictions.le("jobSubmitTimestamp", filter.getTo()));
        }
        if (StringUtils.isNotBlank(filter.getBookName()))
        {
            criteria.add(Restrictions.like("book.proviewDisplayName", filter.getBookName()).ignoreCase());
        }
        if (StringUtils.isNotBlank(filter.getTitleId()))
        {
            criteria.add(Restrictions.like("book.titleId", filter.getTitleId()).ignoreCase());
        }
        if (filter.getBookDefinitionId() != null)
        {
            criteria.add(Restrictions.eq(EBOOK_DEF_ID, filter.getBookDefinitionId()));
        }
        if (StringUtils.isNotBlank(filter.getIsbn()))
        {
            criteria.add(Restrictions.like("book.isbn", filter.getIsbn()).ignoreCase());
        }
        return criteria;
    }

    private Disjunction getSuccessfulPublishStatusRestriction()
    {
        return Restrictions.or(
            Restrictions.eq(PUBLISH_STATUS, PublishingStats.SEND_EMAIL_COMPLETE).ignoreCase(),
            Restrictions.eq(PUBLISH_STATUS, PublishingStats.SEND_EMAIL_COMPLETE_XPP).ignoreCase(),
            Restrictions.eq(PUBLISH_STATUS, PublishingStats.SUCCESFULL_PUBLISH_STATUS).ignoreCase());
    }
}
