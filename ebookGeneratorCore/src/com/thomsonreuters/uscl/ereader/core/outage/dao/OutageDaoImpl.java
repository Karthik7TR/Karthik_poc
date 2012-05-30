package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public class OutageDaoImpl implements OutageDao {
	
	private SessionFactory sessionFactory;
	
	public OutageDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Returns all Outage entities that are scheduled for current and future.
	 */
	@SuppressWarnings("unchecked")
	public List<PlannedOutage> getAllActiveAndScheduledPlannedOutages() {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(PlannedOutage.class)
				.add(Restrictions.ge("endTime", new Date()))
				.addOrder(Order.desc("startTime"))
				.list();
	}
	
	/**
	 * Returns all Outage entities including past outages sorted descending start time.
	 */
	@SuppressWarnings("unchecked")
	public List<PlannedOutage> getAllPlannedOutages() {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(PlannedOutage.class)
				.addOrder(Order.desc("startTime"))
				.list();
	}
	
	/**
	 * Get the Outage entity with the give id.
	 */
	public PlannedOutage findPlannedOutageByPrimaryKey(Long id) {
		return (PlannedOutage) sessionFactory.getCurrentSession().get(
				PlannedOutage.class, id);
	}
	
	/**
	 * Save the Outage entity in the database.
	 * Used for update and create.
	 */
	public void savePlannedOutage(PlannedOutage outage) {
		Session session = sessionFactory.getCurrentSession();
		outage.setLastUpdated(new Date());
		session.saveOrUpdate(outage);
		session.flush();
	}
	
	/**
	 * Delete the Outage entity in the database.
	 */
	public void deletePlannedOutage(PlannedOutage outage) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(outage);
		session.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<OutageType> getAllOutageType() {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(OutageType.class)
				.addOrder(Order.asc("system"))
				.list();
	}
	
	public OutageType findOutageTypeByPrimaryKey(Long id) {
		return (OutageType) sessionFactory.getCurrentSession().get(
				OutageType.class, id);
	}
	
	public void saveOutageType(OutageType outageType) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(outageType);
		session.flush();
	}
	
	public void deleteOutageType(OutageType outageType) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(outageType);
		session.flush();
	}

}
