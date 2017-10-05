package com.thomsonreuters.uscl.ereader.smoketest.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;

/**
 * DAO to test DB connection
 *
 */

public class SmokeTestDaoImpl implements SmokeTestDao {
    private SessionFactory sessionFactory;

    private static Logger LOG = LogManager.getLogger(SmokeTestDaoImpl.class);

    public SmokeTestDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public boolean testConnection() {
        boolean status = false;
        try {
            status = sessionFactory.getCurrentSession().doReturningWork(new ReturningWork<Boolean>() {
                @Override
                public Boolean execute(final Connection connection) throws SQLException {
                    return connection.isValid(5000);
                }
            });
        } catch (final HibernateException e) {
            status = false;
            LOG.error(e.getMessage(), e);
        }
        return status;
    }
}
