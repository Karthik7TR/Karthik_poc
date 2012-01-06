/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;

public class ImageDaoImpl implements ImageDao {
	//private static final Logger log = Logger.getLogger(ImageDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public ImageDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ImageMetadataEntity.class);
		Criterion criterion = Restrictions.eq("primaryKey.jobInstanceId", jobInstanceId);
		criteria.add(criterion);
		return criteria.list();
	}
	
	@Override
	public ImageMetadataEntityKey saveImageMetadata(ImageMetadataEntity metadata) {
		Session session = sessionFactory.getCurrentSession();
		return (ImageMetadataEntityKey) session.save(metadata);
	}
}
