/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;

public class ImageDaoImpl implements ImageDao {
	//private static final Logger log = Logger.getLogger(ImageDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public ImageDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImageMetadataEntity.class);
		criteria.add(Restrictions.eq("jobInstanceId", jobInstanceId));
		return criteria.list();
	}
	
	@Override
	public void saveImageMetadata(ImageMetadataEntity metadata) {
		sessionFactory.getCurrentSession().save(metadata);
	}
}
