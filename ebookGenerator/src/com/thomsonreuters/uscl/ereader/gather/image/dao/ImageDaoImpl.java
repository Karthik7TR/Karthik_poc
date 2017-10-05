package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class ImageDaoImpl implements ImageDao {
    private static final Logger log = LogManager.getLogger(ImageDaoImpl.class);
    private SessionFactory sessionFactory;

    public ImageDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public List<ImageMetadataEntity> findImageMetadata(final long jobInstanceId) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(ImageMetadataEntity.class);
        final Criterion criterion = Restrictions.eq("primaryKey.jobInstanceId", jobInstanceId);
        criteria.add(criterion);
        return criteria.list();
    }

    @Override
    public ImageMetadataEntity findImageMetadataByPrimaryKey(final ImageMetadataEntityKey pk) {
        final Session session = sessionFactory.getCurrentSession();
        return (ImageMetadataEntity) session.get(ImageMetadataEntity.class, pk);
    }

    @Override
    public ImageMetadataEntityKey saveImageMetadata(final ImageMetadataEntity metadata) {
        final Session session = sessionFactory.getCurrentSession();
        log.debug(metadata);
        return (ImageMetadataEntityKey) session.save(metadata);
    }
}
