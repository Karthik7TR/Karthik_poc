package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

public class JobRequestDaoImpl implements JobRequestDao {
    //private static final Logger log = LogManager.getLogger(JobRequestDaoImpl.class);
    private SessionFactory sessionFactory;

    public JobRequestDaoImpl(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void deleteJobRequest(final long jobRequestId) {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findByPrimaryKey(jobRequestId));
        session.flush();
    }

    @Override
    public List<JobRequest> findAllJobRequests() {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JobRequest.class);
        return criteria.list();
    }

    @Override
    public JobRequest findByPrimaryKey(final long jobRequestId) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        final JobRequest jobRequest = (JobRequest) session.get(JobRequest.class, jobRequestId);

        return jobRequest;
    }

    @Override
    public JobRequest findJobRequestByBookDefinitionId(final long ebookDefinitionId) {
        final List<JobRequest> jobRequestList = sessionFactory.getCurrentSession()
            .createCriteria(JobRequest.class)
            .add(Restrictions.eq("bookDefinition.ebookDefinitionId", ebookDefinitionId))
            .list();

        if (jobRequestList.size() > 0) {
            return jobRequestList.get(0);
        }
        return null;
    }

    @Override
    public Long saveJobRequest(final JobRequest jobRequest) {
        final Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(jobRequest);
    }

    @Override
    public void updateJobPriority(final long jobRequestId, final int jobPriority) {
        final JobRequest jobRequest = findByPrimaryKey(jobRequestId);
        jobRequest.setPriority(jobPriority);
        final Session session = sessionFactory.getCurrentSession();
        session.save(jobRequest);
    }

    @Override
    public List<JobRequest> findAllJobRequestsOrderByPriorityAndSubmitedtime() {
        final StringBuffer hql =
            new StringBuffer("select jr from JobRequest jr order by job_priority desc,job_submit_timestamp asc");

        // Create query and populate it with where clause values
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createQuery(hql.toString());

        return query.list();
    }

    @Override
    public void deleteAllJobRequests() {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM JobRequest")
                .executeUpdate();
    }
}
