package com.thomsonreuters.uscl.ereader.jms.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.jms.handler.EBookRequest;

public class BundleArchiveDaoImpl implements BundleArchiveDao {
	private static final Logger log = LogManager.getLogger(BundleArchiveDaoImpl.class);
	private SessionFactory sessionFactory;

	public BundleArchiveDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Long saveRequest(EBookRequest ebookRequest) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(ebookRequest);
	}

	@Override
	public EBookRequest findByPrimaryKey(long ebookRequestId) {
		Session session = sessionFactory.getCurrentSession();
		EBookRequest request = (EBookRequest) session.get(EBookRequest.class, ebookRequestId);

		return request;
	}

	@Override
	public void deleteRequest(long ebookRequestId) {
		// possible TODO: log more human-readable information about the request being deleted. (requires a table read)
		log.warn(String.format("Removing request %d from the Bundle Archive.", ebookRequestId));
		Session session = sessionFactory.getCurrentSession();
		session.delete(findByPrimaryKey(ebookRequestId));
		session.flush();
	}

	@Override
	@SuppressWarnings("unchecked")
	public EBookRequest findByRequestId(String messageId) {
		List<EBookRequest> eBookRequestList = sessionFactory.getCurrentSession().createCriteria(EBookRequest.class).add(Restrictions.eq(
				"eBookRequest.messageId", messageId)).list();

		if (eBookRequestList.size() > 0) {
			return eBookRequestList.get(0);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EBookRequest> findAllRequests() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EBookRequest.class);
		return (List<EBookRequest>) criteria.list();
	}
}