/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.MediaType;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class ImageServiceTest {
	//private static final Logger log = LogManager.getLogger(ImageServiceTest.class);
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private static final Long JOB_INSTANCE_ID = 1965l;
	private static final String GUID = "junitBogusGuid";
	private static final String DOC_GUID = "dummyDocGuid";
	private static final ImageMetadataEntityKey METADATA_PK = new ImageMetadataEntityKey(JOB_INSTANCE_ID, GUID, DOC_GUID);
	private ImageDao mockImageDao;
	private ImageServiceImpl imageService;
	
	@Before
	public void setUp() throws Exception {		
		this.mockImageDao = EasyMock.createMock(ImageDao.class);
		
		this.imageService = new ImageServiceImpl();
		imageService.setImageDao(mockImageDao);
	}

	
	
	protected ImgMetadataInfo getImgMetadata(){
		ImgMetadataInfo imgMetadataInfo = new ImgMetadataInfo();
		Long longValue = new Long(10);
		String stringValue = "10";
		imgMetadataInfo.setDimUnit(stringValue);
		imgMetadataInfo.setDocGuid(DOC_GUID);
		imgMetadataInfo.setDpi(longValue);
		imgMetadataInfo.setImgGuid(stringValue);
		imgMetadataInfo.setHeight(longValue);
		imgMetadataInfo.setSize(longValue);
		imgMetadataInfo.setMimeType("img");
		imgMetadataInfo.setWidth(longValue);
		
		return imgMetadataInfo;
	}
	

	
	@Test
	public void testSaveImageMetadataEntity() {
		ImageMetadataEntity entity = new ImageMetadataEntity(METADATA_PK, "titleId",
				   									100l, 200l, 41234l, 1111l, "px", MediaType.IMAGE_PNG);
		EasyMock.expect(mockImageDao.saveImageMetadata(entity)).andReturn(METADATA_PK);
		EasyMock.replay(mockImageDao);
		
		ImageMetadataEntityKey pk = imageService.saveImageMetadata(entity);
		Assert.assertNotNull(pk);
		Assert.assertEquals(METADATA_PK, pk);
		
		EasyMock.verify(mockImageDao);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFindImageMetadata() {
		long jobInstanceId = System.currentTimeMillis();
		EasyMock.expect(mockImageDao.findImageMetadata(jobInstanceId)).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(mockImageDao);
		
		List<ImageMetadataEntity> list = imageService.findImageMetadata(jobInstanceId);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		EasyMock.verify(mockImageDao);
	}
}
