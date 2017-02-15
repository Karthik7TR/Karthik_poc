package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import org.hibernate.SessionFactory;

public class AppParameterDaoImpl implements AppParameterDao
{
    private SessionFactory sessionFactory;

    public AppParameterDaoImpl(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public AppParameter findByPrimaryKey(final String key)
    {
        final AppParameter param = (AppParameter) sessionFactory.getCurrentSession().get(AppParameter.class, key);
        return param;
    }

    @Override
    public void save(final AppParameter param)
    {
        param.setLastUpdated(new Date());
        sessionFactory.getCurrentSession().saveOrUpdate(param);
    }

    @Override
    public void delete(final AppParameter param)
    {
        sessionFactory.getCurrentSession().delete(param);
    }
}
