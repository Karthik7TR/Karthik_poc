package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
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

        final String orderByColumn = getOrderByColumnName(sort.getSortProperty());
        if (sort.isAscending()) {
            criteria.addOrder(Order.asc(orderByColumn));
        } else {
            criteria.addOrder(Order.desc(orderByColumn));
        }

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
    private String getOrderByColumnName(final SortProperty sortProperty) {
        switch (sortProperty) {
        case PROVIEW_REQUEST:
            return "proviewRequest";
        case REQUEST_DATE:
            return "requestDate";
        case BOOK_VERSION:
            return "bookVersion";
        case TITLE_ID:
            return "titleId";
        case USERNAME:
            return "username";
        case BOOK_LAST_UPDATED:
            return "bookLastUpdated";
        default:
            throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
        }
    }
}
