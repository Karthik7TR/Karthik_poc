package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.util.CriteriaFunctionOrder;
import com.thomsonreuters.uscl.ereader.util.CriteriaFunctionOrder.SqlFunctionCall;
import com.thomsonreuters.uscl.ereader.util.CriteriaFunctionOrder.SqlFunctionWithArgsCall;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO to manage ProviewAudit entities.
 *
 */

public class ProviewAuditDaoImpl implements ProviewAuditDao {
    private SessionFactory sessionFactory;

    public ProviewAuditDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public String getBookStatus(final String titleId, final String version) {
        final StringBuffer hql =
            new StringBuffer("select pa from ProviewAudit pa where (pa.titleId,pa.bookVersion,pa.requestDate) in ");
        hql.append("(select pa2.titleId,pa2.bookVersion,max(pa2.requestDate) from ProviewAudit pa2 where");
        hql.append(" pa2.titleId = :titleId");
        hql.append(" and pa2.bookVersion =  :version");
        hql.append(" and pa2.proviewRequest not in ('PROMOTE','REVIEW') group by pa2.titleId,pa2.bookVersion)");
        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());
        query.setParameter("titleId", titleId);
        query.setParameter("version", version);

        final ProviewAudit audit = (ProviewAudit) query.uniqueResult();
        if (audit == null) {
            return null;
        }
        return audit.getProviewRequest();
    }

    @Override
    public List<ProviewAudit> findRemovedAndDeletedVersions(final String titleId) {
        final StringBuffer hql =
            new StringBuffer("select pa from ProviewAudit pa where (pa.titleId,pa.bookVersion,pa.requestDate) in ");
        hql.append("(select pa2.titleId,pa2.bookVersion,max(pa2.requestDate) from ProviewAudit pa2 where");
        hql.append(" pa2.titleId = :titleId");
        hql.append(" and pa2.proviewRequest not in ('PROMOTE','REVIEW')  ");
        hql.append(" group by pa2.titleId,pa2.bookVersion) order by pa.id");
        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());
        query.setParameter("titleId", titleId);

        return query.list();
    }

    @Override
    public List<ProviewAudit> findJobSubmitterNameForAllTitlesLatestVersion() {
        final StringBuffer hql =
                new StringBuffer("select x.title_id, x.book_version, x.proview_request, x.request_date, x.user_name from ( ");
        hql.append(" select a.title_id, a.book_version, ");
        hql.append(" case a.proview_request WHEN 'REMOVE' THEN 'Removed' WHEN 'PROMOTE' THEN 'Final' ELSE 'Review' end as proview_request, ");
        hql.append(" a.request_date, a.user_name, ");
        hql.append(" row_number() over(partition by a.title_id ");
        hql.append(" order by a.book_version desc, ");
        hql.append(" case a.proview_request WHEN 'REMOVE' THEN 1 WHEN 'PROMOTE' THEN 2 ELSE 3 end, a.proview_audit_id desc) proview_rank ");
        hql.append(" from proview_audit a ");
        //Remove DELETE
        hql.append(" where a.proview_request in ('REVIEW','PROMOTE','REMOVE') ) x ");
        hql.append(" where x.proview_rank = 1 ");
        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        //final Query query = session.createQuery(hql.toString());
        //return query.list();

        final Query query = session.createSQLQuery(hql.toString());
        final List<Object[]> objectList = query.list();

        final List<ProviewAudit> arrayList = new ArrayList<>();
        for (final Object[] arr : objectList) {
            final ProviewAudit proviewAudit =
                  new ProviewAudit(arr[0].toString(), arr[1].toString(), arr[2].toString(),
                      (Date) arr[3], arr[4].toString());
            arrayList.add(proviewAudit);
        }
        return arrayList;

    }

    @Override
    public void save(ProviewAudit audit) {
        final Session session = sessionFactory.getCurrentSession();

        if (audit.getId() != null) {
            audit = (ProviewAudit) session.merge(audit);
        } else {
            session.save(audit);
        }

        session.flush();
    }

    @Override
    public List<ProviewAudit> findProviewAudits(final ProviewAuditFilter filter, final ProviewAuditSort sort) {
        final Criteria criteria = addFilters(filter);

        addOrderByColumnNames(criteria, sort);

        final int itemsPerPage = sort.getItemsPerPage();
        criteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage));
        criteria.setMaxResults(itemsPerPage);

        return criteria.list();
    }

    @Override
    public int numberProviewAudits(final ProviewAuditFilter filter) {
        final Criteria criteria = addFilters(filter);

        criteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id"));

        return criteria.list().size();
    }

    @Override
    public Map<String, Date> findMaxRequestDateByTitleIds(final Collection<String> titleIds) {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviewAudit.class);
        criteria.setProjection(Projections.projectionList()
            .add(Projections.groupProperty("titleId"))
            .add(Projections.max("requestDate")));

        //Since Oracle db has a restriction for IN clause (it can contain only 1000 values)
        //we are using a workaround like connecting several clauses (in (0...999) or in (1000...1999)...)
        //So we need to split collection with titleIds to parts with size 1000
        ListUtils.partition(new ArrayList<>(titleIds), 1000).stream()
            .map(bunch -> Restrictions.in("titleId", bunch))
            .reduce(Restrictions::or)
            .ifPresent(criteria::add);

        final List<Object[]> resultList = criteria.list();
        return resultList.stream()
            .collect(Collectors.toMap(columns -> columns[0].toString(), columns -> (Date) columns[1]));
    }

    private Criteria addFilters(final ProviewAuditFilter filter) {
        final Session session = sessionFactory.getCurrentSession();

        final Criteria criteria = session.createCriteria(ProviewAudit.class);

        if (filter.getFrom() != null) {
            criteria.add(Restrictions.ge("requestDate", filter.getFrom()));
        }
        if (filter.getTo() != null) {
            criteria.add(Restrictions.le("requestDate", filter.getTo()));
        }
        if (StringUtils.isNotBlank(filter.getAction())) {
            criteria.add(Restrictions.eq("proviewRequest", filter.getAction()));
        }
        if (StringUtils.isNotBlank(filter.getTitleId())) {
            criteria.add(Restrictions.like("titleId", filter.getTitleId()).ignoreCase());
        }
        if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
            criteria.add(Restrictions.like("username", filter.getSubmittedBy()).ignoreCase());
        }

        return criteria;
    }

    /**
     * Map the sort column enumeration into the actual column identifier used in the criteria.
     * @param sortProperty enumerated value that reflects the database table sort column to sort on.
     */
    private void addOrderByColumnNames(final Criteria criteria, final ProviewAuditSort sort) {
        final SortProperty sortProperty = sort.getSortProperty();
        switch (sortProperty) {
        case PROVIEW_REQUEST:
            criteria.addOrder(getSimpleOrder("proviewRequest", sort.isAscending()));
            break;
        case REQUEST_DATE:
            criteria.addOrder(getSimpleOrder("requestDate", sort.isAscending()));
            break;
        case USERNAME:
            criteria.addOrder(getSimpleOrder("username", sort.isAscending()));
            break;
        case BOOK_LAST_UPDATED:
            criteria.addOrder(getSimpleOrder("bookLastUpdated", sort.isAscending()));
            break;
        case TITLE_ID:
            criteria.addOrder(getTitleIdOrder("titleId", sort.isAscending()));
            break;
        case BOOK_VERSION:
            final String propertyName = "bookVersion";
            criteria.addOrder(getVersionOrder(propertyName, true, sort.isAscending()));
            criteria.addOrder(getVersionOrder(propertyName, false, sort.isAscending()));
            break;
        default:
            throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
        }
    }

    private Order getVersionOrder(final String propertyName, final boolean isMajorVersion, final boolean isAscending) {
        final int occurence = isMajorVersion ? 1 : 2;
        final SqlFunctionCall sqlFunctionCall = SqlFunctionCall.toNumber()
            .includeFunction(SqlFunctionWithArgsCall.regexpSubstr("\\d+", 1, occurence));
        return isAscending ? CriteriaFunctionOrder.asc(propertyName, sqlFunctionCall)
            : CriteriaFunctionOrder.desc(propertyName, sqlFunctionCall);
    }

    private Order getTitleIdOrder(final String propertyName, final boolean isAscending) {
        return isAscending ? CriteriaFunctionOrder.asc(propertyName, SqlFunctionCall.trim())
            : CriteriaFunctionOrder.desc(propertyName, SqlFunctionCall.trim());
    }

    private Order getSimpleOrder(final String propertyName, final boolean isAscending) {
        return isAscending ? Order.asc(propertyName) : Order.desc(propertyName);
    }
}
