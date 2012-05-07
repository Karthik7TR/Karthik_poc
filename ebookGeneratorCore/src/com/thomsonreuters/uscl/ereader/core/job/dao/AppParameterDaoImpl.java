package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;

import org.hibernate.SessionFactory;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;

public class AppParameterDaoImpl implements AppParameterDao {

	private SessionFactory sessionFactory;
	
	public AppParameterDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public AppParameter findByPrimaryKey(String key) {
		AppParameter param = (AppParameter) sessionFactory.getCurrentSession().get(AppParameter.class, key);
		return param;
	}
	@Override
	public void save(AppParameter param) {
		param.setLastUpdated(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(param);
	}

	@Override
	public void delete(AppParameter param) {
		sessionFactory.getCurrentSession().delete(param);
	}
}
