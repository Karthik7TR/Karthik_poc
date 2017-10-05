package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ImageDaoTest {
    private static long JOB_INSTANCE_ID = 1965;

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Criteria mockCriteria;
    private ImageDaoImpl dao;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new ImageDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindImageMetadata() {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(ImageMetadataEntity.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add((Criterion) EasyMock.anyObject())).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(Collections.EMPTY_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<ImageMetadataEntity> entities = dao.findImageMetadata(JOB_INSTANCE_ID);
        Assert.assertNotNull(entities);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testSaveImageMetadata() {
        final String bogusGuid = "someBogusGuid";
        final String docGuid = "dummyDocGuid";
        final ImageMetadataEntityKey expectedPk = new ImageMetadataEntityKey(JOB_INSTANCE_ID, bogusGuid, docGuid);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.save(EasyMock.anyObject())).andReturn(expectedPk);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final ImageMetadataEntityKey actualPk = dao.saveImageMetadata(new ImageMetadataEntity());
        Assert.assertEquals(expectedPk, actualPk);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }
}
