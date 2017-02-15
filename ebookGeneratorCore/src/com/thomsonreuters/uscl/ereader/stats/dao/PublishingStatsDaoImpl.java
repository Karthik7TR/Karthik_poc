package com.thomsonreuters.uscl.ereader.stats.dao;

import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Projections.property;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.lt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
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

    // @Required
    // public void setSessionFactory(SessionFactory sessionfactory) {
    // this.sessionFactory = sessionfactory;
    // }

    public PublishingStatsDaoImpl(final SessionFactory hibernateSessionFactory)
    {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public PublishingStats findStatsByLastUpdated(final Long jobId)
    {
        final StringBuffer hql = new StringBuffer(
            "select ps from PublishingStats ps where ps.lastUpdated = (select max(ps2.lastUpdated) from PublishingStats ps2 where ps.ebookDefId = ps2.ebookDefId ");
        hql.append(" and ps2.gatherTocNodeCount is not null)  ");
        hql.append(" and ps.ebookDefId =  ");
        hql.append(jobId);
        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());

        final PublishingStats pubStats = (PublishingStats) query.uniqueResult();
        return (pubStats);
    }

    @Override
    public PublishingStats findJobStatsByPubStatsPK(final PublishingStatsPK jobIdPK)
    {
        final Session session = sessionFactory.getCurrentSession();
        return (PublishingStats) session.get(PublishingStats.class, jobIdPK);
    }

    @Override
    public PublishingStats findJobStatsByJobId(final Long JobId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final PublishingStats pubStats = (PublishingStats) session.createCriteria(PublishingStats.class)
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("jobInstanceId", JobId))
            .uniqueResult();

        return (pubStats);
    }

    @Override
    public Map<String, String> findSubGroupByVersion(final Long boofDefnition)
    {
        final StringBuffer hql = new StringBuffer(
            "select ps.BOOK_VERSION_SUBMITTED, a.SUBGROUP_HEADING from PUBLISHING_STATS ps inner join(");
        hql.append(
            " select BOOK_VERSION_SUBMITTED,max(last_updated) as maxt from PUBLISHING_STATS where ebook_definition_id=");
        hql.append(boofDefnition);
        hql.append(" group by BOOK_VERSION_SUBMITTED) b on b.maxt = ps.last_updated");
        hql.append(" join ebook_audit a on a.AUDIT_ID = ps.AUDIT_ID order by ps.BOOK_VERSION_SUBMITTED ");

        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createSQLQuery(hql.toString());
        final List<Object[]> objectList = query.list();

        final Map<String, String> eBookSubgroupVersionMap = new HashMap<>();

        for (final Object[] arr : objectList)
        {
            final String bookVersionSubmitted;
            final String subGroupHeading;
            bookVersionSubmitted = "v" + arr[0].toString();
            if (arr[1] != null)
            {
                subGroupHeading = arr[1].toString();
                eBookSubgroupVersionMap.put(bookVersionSubmitted, subGroupHeading);
            }
        }
        return eBookSubgroupVersionMap;
    }

    @Override
    public String findNameByIdAndVersion(final Long boofDefnition, final String version)
    {
        final StringBuffer hql = new StringBuffer("select a.PROVIEW_DISPLAY_NAME from PUBLISHING_STATS ps inner join(");
        hql.append(
            " select BOOK_VERSION_SUBMITTED,max(last_updated) as maxt from PUBLISHING_STATS where ebook_definition_id=");
        hql.append(boofDefnition);
        hql.append(" and BOOK_VERSION_SUBMITTED= '");
        hql.append(StringUtils.substringAfter(version, "v") + "'");
        hql.append(" group by BOOK_VERSION_SUBMITTED) b on b.maxt = ps.last_updated");
        hql.append(" join ebook_audit a on a.AUDIT_ID = ps.AUDIT_ID and a.ebook_definition_id= ");
        hql.append(boofDefnition);

        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createSQLQuery(hql.toString());
        final String proviewDisplayName = (String) query.uniqueResult();
        return proviewDisplayName;
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
        toRemove = (PublishingStats) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(toRemove);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public Long getMaxGroupVersionById(final Long eBookDefId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Query query =
            session.createQuery("select max(groupVersion) from PublishingStats " + "where ebookDefId = :ebookDefId ");
        query.setParameter("ebookDefId", eBookDefId);
        return (Long) query.uniqueResult();
    }

    @Override
    public EbookAudit getMaxAuditId(final Long eBookDefId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Query query =
            session.createQuery("select max(audit) from PublishingStats " + "where ebookDefId = :ebookDefId ");
        query.setParameter("ebookDefId", eBookDefId);
        return (EbookAudit) query.uniqueResult();
    }

    @Override
    public int updateJobStats(final PublishingStats jobstats, final StatsUpdateTypeEnum updateType)
    {
        final StringBuffer hql = new StringBuffer("update PublishingStats set   ");
        if (updateType.equals(StatsUpdateTypeEnum.GATHERTOC) || updateType.equals(StatsUpdateTypeEnum.GENERATETOC))
        {
            hql.append("gatherTocNodeCount = ");
            hql.append(jobstats.getGatherTocNodeCount());
            hql.append(", gatherTocDocCount = ");
            hql.append(jobstats.getGatherTocDocCount());
            hql.append(", gatherTocRetryCount = ");
            hql.append(jobstats.getGatherTocRetryCount());
            hql.append(", gatherTocSkippedCount = ");
            hql.append(jobstats.getGatherTocSkippedCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.GATHERDOC) || updateType.equals(StatsUpdateTypeEnum.GENERATEDOC))
        {
            hql.append("gatherDocExpectedCount = ");
            hql.append(jobstats.getGatherDocExpectedCount());
            hql.append(", gatherDocRetrievedCount = ");
            hql.append(jobstats.getGatherDocRetrievedCount());
            hql.append(", gatherDocRetryCount = ");
            hql.append(jobstats.getGatherDocRetryCount());
            hql.append(", gatherMetaExpectedCount = ");
            hql.append(jobstats.getGatherMetaExpectedCount());
            hql.append(", gatherMetaRetrievedCount = ");
            hql.append(jobstats.getGatherMetaRetrievedCount());
            hql.append(", gatherMetaRetryCount = ");
            hql.append(jobstats.getGatherMetaRetryCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.GATHERIMAGE))
        {
            hql.append("gatherImageExpectedCount = ");
            hql.append(jobstats.getGatherImageExpectedCount());
            hql.append(", gatherImageRetrievedCount = ");
            hql.append(jobstats.getGatherImageRetrievedCount());
            hql.append(", gatherImageRetryCount = ");
            hql.append(jobstats.getGatherImageRetryCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.TITLEDOC))
        {
            hql.append("titleDocCount = ");
            hql.append(jobstats.getTitleDocCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.TITLEDUPDOCCOUNT))
        {
            hql.append("titleDupDocCount = ");
            hql.append(jobstats.getTitleDupDocCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.FORMATDOC))
        {
            hql.append("formatDocCount = ");
            hql.append(jobstats.getFormatDocCount());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.ASSEMBLEDOC))
        {
            hql.append("bookSize = ");
            hql.append(jobstats.getBookSize());
            hql.append(", largestDocSize = ");
            hql.append(jobstats.getLargestDocSize());
            hql.append(", largestImageSize = ");
            hql.append(jobstats.getLargestImageSize());
            hql.append(", largestPdfSize = ");
            hql.append(jobstats.getLargestPdfSize());
        }
        else if (updateType.equals(StatsUpdateTypeEnum.FINALPUBLISH))
        {
            hql.append("publishEndTimestamp = sysdate");
        }
        else if (updateType.equals(StatsUpdateTypeEnum.GENERAL))
        {
            //Intentionally left blank
        }
        else if (updateType.equals(StatsUpdateTypeEnum.GROUPEBOOK))
        {
            hql.append("groupVersion = ");
            hql.append(jobstats.getGroupVersion());
        }

        else
        {
            LOG.error("Unknown StatsUpdateTypeEnum");
            // TODO: failure logic
        }
        if (StringUtils.isNotBlank(jobstats.getPublishStatus()))
        {
            if (updateType.equals(StatsUpdateTypeEnum.GENERAL))
            {
                hql.append(String.format("publishStatus = '%s'", jobstats.getPublishStatus()));
            }
            else
            {
                hql.append(String.format(", publishStatus = '%s'", jobstats.getPublishStatus()));
            }
        }
        else
        {
            hql.append(", publishStatus = null");
        }

        hql.append(", lastUpdated = sysdate ");

        hql.append(" where jobInstanceId =  "); // WHERE clause
        hql.append(jobstats.getJobInstanceId());

        LOG.debug("hql.toString() =" + hql.toString());
        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());

        // query.setLong(0, jobstats.getJobInstanceId());

        int result = 0;
        try
        {
            result = query.executeUpdate();
            session.flush();
        }
        catch (final HibernateException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public EbookAudit findAuditInfoByJobId(final Long jobId)
    {
        final StringBuffer hql = new StringBuffer("select ea  from PublishingStats ps, EbookAudit ea ");

        hql.append(" where ps.jobInstanceId =  "); // WHERE clause
        hql.append(jobId);
        // hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId "
        // );
        hql.append(" and ps.audit.auditId = ea.auditId ");

        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());

        final EbookAudit auditResult = (EbookAudit) query.uniqueResult();
        return (auditResult);
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
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("ebookDefId", EbookDefId));

        return criteria.list();
    }

    @Override
    public List<PublishingStats> findPubStatsByEbookDefSort(final Long EbookDefId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .add(Restrictions.eq("ebookDefId", EbookDefId))
            .addOrder(Order.desc("jobInstanceId"));
        return criteria.list();
    }

    @Override
    public List<String> findSuccessfullyPublishedIsbnByTitleId(final String titleId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("book.titleId", titleId))
            .add(Restrictions.in(
                "publishStatus",
                new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
            .setProjection(Projections.distinct(Projections.property("book.isbn")));
        return criteria.list();
    }

    @Override
    public List<String> findSuccessfullyPublishedsubGroupById(final Long ebookDefId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
            .add(Restrictions.in(
                "publishStatus",
                new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
            .setProjection(Projections.distinct(Projections.property("book.subGroupHeading")));
        return criteria.list();
    }

    @Override
    public Long findSuccessfullyPublishedGroupBook(final Long ebookDefId)
    {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(PublishingStats.class)
            .createAlias("audit", "book")
            .setFetchMode("audit", FetchMode.JOIN)
            .add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
            .add(Restrictions.in(
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

        final String orderByColumn = getOrderByColumnName(sort.getSortProperty());
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
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobInstanceId)
    {
        final Session session = sessionFactory.getCurrentSession();
        final DetachedCriteria subQuery = DetachedCriteria.forClass(PublishingStats.class)
            .add(eq("jobInstanceId", jobInstanceId))
            .setProjection(property("ebookDefId"));
        final Criteria criteria =
            session.createCriteria(PublishingStats.class)
                .add(Property.forName("ebookDefId").eq(subQuery))
                .add(in(
                    "publishStatus",
                    new String[] {PublishingStats.SEND_EMAIL_COMPLETE, PublishingStats.SUCCESFULL_PUBLISH_STATUS}))
                //jobInstanceId of prev job instances are less then current one
                .add(lt("jobInstanceId", jobInstanceId))
                .addOrder(desc("jobInstanceId"))
                .setMaxResults(1);
        return (PublishingStats) criteria.uniqueResult();
    }

    /**
     * Map the sort column enumeration into the actual column identifier used in
     * the HQL query.
     *
     * @param sortProperty
     *            enumerated value that reflects the database table sort column
     *            to sort on.
     */
    private String getOrderByColumnName(final SortProperty sortProperty)
    {
        switch (sortProperty)
        {
        case AUDIT_ID:
            return "book.auditId";
        case BOOK_SIZE:
            return "bookSize";
        case BOOK_VERSION:
            return "bookVersionSubmitted";
        case EBOOK_DEFINITION_ID:
            return "ebookDefId";
        case JOB_INSTANCE_ID:
            return "jobInstanceId";
        case JOB_SUBMIT_TIMESTAMP:
            return "jobSubmitTimestamp";
        case JOB_SUBMITTER:
            return "jobSubmitterName";
        case LARGEST_DOC_SIZE:
            return "largestDocSize";
        case LARGEST_IMAGE_SIZE:
            return "largestImageSize";
        case LARGEST_PDF_SIZE:
            return "largestPdfSize";
        case PUBLISH_STATUS:
            return "publishStatus";
        case PROVIEW_DISPLAY_NAME:
            return "book.proviewDisplayName";
        case TITLE_ID:
            return "book.titleId";
        default:
            throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
        }
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

    @Override
    public List<PublishingStats> findAllPublishingStats()
    {
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery("from PublishingStats");
        return query.list();
    }
}
