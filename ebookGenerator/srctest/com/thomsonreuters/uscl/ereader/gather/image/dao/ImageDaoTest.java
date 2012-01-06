/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;

public class ImageDaoTest  {
	private static long JOB_INSTANCE_ID = 1965;
	
	private SessionFactory mockSessionFactory;
	private org.hibernate.classic.Session mockSession;
	private Criteria mockCriteria;
	private ImageDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.classic.Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new ImageDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void testFindImageMetadata() {
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(ImageMetadataEntity.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.add((Criterion)EasyMock.anyObject())).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<ImageMetadataEntity> entities = dao.findImageMetadata(JOB_INSTANCE_ID);
		Assert.assertNotNull(entities);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testSaveImageMetadata() {
		String bogusGuid = "someBogusGuid";
		ImageMetadataEntityKey expectedPk = new ImageMetadataEntityKey(JOB_INSTANCE_ID, bogusGuid);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.save((ImageMetadataEntity) EasyMock.anyObject())).andReturn(expectedPk);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		ImageMetadataEntityKey actualPk = dao.saveImageMetadata(new ImageMetadataEntity());
		Assert.assertEquals(expectedPk, actualPk);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
}
