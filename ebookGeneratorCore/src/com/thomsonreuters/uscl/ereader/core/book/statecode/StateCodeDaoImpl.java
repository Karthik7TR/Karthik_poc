package com.thomsonreuters.uscl.ereader.core.book.statecode;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.jetbrains.annotations.NotNull;

public class StateCodeDaoImpl implements StateCodeDao {
    private SessionFactory sessionFactory;

    public StateCodeDaoImpl(final SessionFactory sessFactory) {
        sessionFactory = sessFactory;
    }

    @Override
    public void createStateCode(@NotNull final StateCode stateCode) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(stateCode);
        session.flush();
    }

    @Override
    public void updateStateCode(@NotNull final StateCode stateCode) {
        final Session session = sessionFactory.getCurrentSession();
        session.merge(stateCode);
        session.flush();
    }

    @Override
    public List<StateCode> getAllStateCodes() {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(StateCode.class).addOrder(Order.asc("name"));
        return Collections.checkedList(criteria.list(), StateCode.class);
    }

    @Override
    public StateCode getStateCodeById(@NotNull final Long stateCodeId) {
        return (StateCode) sessionFactory.getCurrentSession().get(StateCode.class, stateCodeId);
    }

    @Override
    public StateCode getStateCodeByName(@NotNull final String stateCodeName) {
        return (StateCode) sessionFactory.getCurrentSession()
            .createCriteria(StateCode.class)
            .add(eq("name", stateCodeName).ignoreCase())
            .uniqueResult();
    }

    @Override
    public void deleteStateCode(@NotNull final StateCode stateCode) {
        final StateCode merged = (StateCode) sessionFactory.getCurrentSession().merge(stateCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(merged);
        session.flush();
    }
}
