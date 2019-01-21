package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

public class OutageDaoImpl implements OutageDao {
    private SessionFactory sessionFactory;

    public OutageDaoImpl(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns all Outage entities that are scheduled for current and future.
     */
    @Override
    public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PlannedOutage.class)
            .add(Restrictions.ge("endTime", new Date()))
            .addOrder(Order.desc("startTime"))
            .list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannedOutage> getAllPlannedOutagesForType(final Long outageTypeId) {
        final Session session = sessionFactory.getCurrentSession();
        final StringBuffer hql = new StringBuffer("select p from PlannedOutage p where p.outageType=" + outageTypeId);

        final Query query = session.createQuery(hql.toString());
        final List<PlannedOutage> outageList = query.list();
        return outageList;
    }

    /**
     * Returns all Outage entities including past outages sorted descending start time.
     */
    @Override
    public List<PlannedOutage> getAllPlannedOutages() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PlannedOutage.class).addOrder(Order.desc("startTime")).list();
    }

    /**
     * Returns all Outage entities that are scheduled and displayed to the user
     * @param endDate - filter used to limit PlannedOutages
     */
    @Override
    public List<PlannedOutage> getAllPlannedOutagesToDisplay(final Date endDate) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PlannedOutage.class)
            .add(Restrictions.ge("endTime", new Date()))
            .add(Restrictions.le("startTime", endDate))
            .addOrder(Order.asc("startTime"))
            .list();
    }

    /**
     * Get the Outage entity with the give id.
     */
    @Override
    public PlannedOutage findPlannedOutageByPrimaryKey(final Long id) {
        return (PlannedOutage) sessionFactory.getCurrentSession().get(PlannedOutage.class, id);
    }

    /**
     * Save the Outage entity in the database.
     * Used for update and create.
     */
    @Override
    public void savePlannedOutage(final PlannedOutage outage) {
        final Session session = sessionFactory.getCurrentSession();
        outage.setLastUpdated(new Date());
        session.saveOrUpdate(outage);
        session.flush();
    }

    /**
     * Delete the Outage entity in the database.
     */
    @Override
    public void deletePlannedOutage(final PlannedOutage outage) {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(outage);
        session.flush();
    }

    @Override
    public List<OutageType> getAllOutageType() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(OutageType.class).addOrder(Order.asc("system")).list();
    }

    @Override
    public OutageType findOutageTypeByPrimaryKey(final Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (OutageType) session.createCriteria(OutageType.class)
            .setFetchMode("plannedOutage", FetchMode.JOIN)
            .add(Restrictions.eq("id", id))
            .uniqueResult();
    }

    @Override
    public void saveOutageType(final OutageType outageType) {
        final Session session = sessionFactory.getCurrentSession();
        outageType.setLastUpdated(new Date());
        session.saveOrUpdate(outageType);
        session.flush();
    }

    @Override
    public void deleteOutageType(final OutageType outageType) {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(outageType);
        session.flush();
    }
}
