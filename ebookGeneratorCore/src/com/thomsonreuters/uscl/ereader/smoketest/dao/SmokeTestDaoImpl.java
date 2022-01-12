package com.thomsonreuters.uscl.ereader.smoketest.dao;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;

/**
 * DAO to test DB connection
 *
 */
@Slf4j
public class SmokeTestDaoImpl implements SmokeTestDao {
    private SessionFactory sessionFactory;

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
            log.error(e.getMessage(), e);
        }
        return status;
    }
}
