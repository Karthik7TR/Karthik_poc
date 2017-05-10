package com.thomsonreuters.uscl.ereader.stats.dao;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.lt;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

public class PublishingStatsDaoImpl implements PublishingStatsDao
{
    private static final Logger LOG = LogManager.getLogger(PublishingStatsDaoImpl.class);
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
    public PublishingStats findJobStatsByJobId(final Long JobId)
    {
        return (PublishingStats) sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("jobInstanceId", JobId))
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
    public void deleteJobStats(PublishingStats toRemove)
    {
        final Session session = sessionFactory.getCurrentSession();
        toRemove = (PublishingStats) session.merge(toRemove);
        session.delete(toRemove);
        session.flush();
    }

    @Override
    public EbookAudit findAuditInfoByJobId(final Long jobId)
    {
        final PublishingStats stats = (PublishingStats) sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .add(Restrictions.eq("jobInstanceId", jobId))
            .uniqueResult();

        EbookAudit ebookAudit = null;
        if(stats != null) {
            ebookAudit = stats.getAudit();
            Hibernate.initialize(ebookAudit);
        }
        return ebookAudit;
    }

    /**
     * Find Publishing stats for ebook
     *
     * @param EbookDefId
     * @return
     */
    @Override
    public List<PublishingStats> findPublishingStatsByEbookDef(final Long EbookDefId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("ebookDefId", EbookDefId));

        return criteria.list();
    }

    @Override
    public List<String> findSuccessfullyPublishedIsbnByTitleId(final String titleId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("book.titleId", titleId))
            .add(
                Restrictions.in(
                    "publishStatus",
                    new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
            .setProjection(Projections.distinct(Projections.property("book.isbn")));
        return criteria.list();
    }

    @Override
    public Long findSuccessfullyPublishedGroupBook(final Long ebookDefId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
            .add(
                Restrictions.in(
                    "publishStatus",
                    new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
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
            .add(eq("jobInstanceId", jobId))
            .setProjection(Projections.property("ebookDefId"));
        final Criteria criteria = session.createCriteria(
            PublishingStats.class)
            .add(Property.forName("ebookDefId").eq(subQuery))
            .add(
                in(
                    "publishStatus",
                    new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
            //jobInstanceId of prev job instances are less then current one
            .add(lt("jobInstanceId", jobId))
            .addOrder(Order.desc("jobInstanceId"))
            .setMaxResults(1);
        return (PublishingStats) criteria.uniqueResult();
    }

    private Criteria addFilters(final PublishingStatsFilter filter)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN);

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
            criteria.add(Restrictions.eq("ebookDefId", filter.getBookDefinitionId()));
        }
        if (StringUtils.isNotBlank(filter.getIsbn()))
        {
            criteria.add(Restrictions.like("book.isbn", filter.getIsbn()).ignoreCase());
        }
        return criteria;
    }
}
