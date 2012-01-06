/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.service.image;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class ImageVerticalServiceIntegrationTest  {
	private static final String GUID = "IA31BCD5F18364C9BBDCD008012AFBF02";
	@Autowired ImageService imageService;

	@Test
	public void testFetchImageMetadata() {
		SingleImageMetadataResponse response = imageService.fetchImageMetadata(GUID);
		Assert.assertNotNull(response);
System.out.println(response);
	}
	
	@Test
	public void testFetchImageMetadataBadGuid() {
		String badGuid = "IA31BCD5F18364C9BBDCD008012AFFFFF";
		try {
			SingleImageMetadataResponse response = imageService.fetchImageMetadata(badGuid);
			Assert.assertNotNull(response);
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchImage() {
		File tmpImageDir = new File (System.getProperty("java.io.tmpdir"));
		long jobInstanceId = 1965;
		String titleId = "bogusTitleId";
		List<String> imageGuids = new ArrayList<String>(1);
		imageGuids.add(GUID);
		try {
			imageService.fetchImages(imageGuids, tmpImageDir, jobInstanceId, titleId);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			
		}
	}
	
	@Test
	public void testPersistImageMetadata() {
		Long jobInstanceId = -999l;
		String titleId = "bogusTitleId";
		String guid = "someBogusTestingGuid";
		Long width = 100l, height = 200l, size = 300l, dpi = 400l;
		String dimUnit = "px";
		ImageMetadataEntityKey pk = new ImageMetadataEntityKey(jobInstanceId, guid);
		ImageMetadataEntity expectedEntity = new ImageMetadataEntity(pk, titleId,
																	 width, height, size, dpi, dimUnit);
		imageService.saveImageMetadata(expectedEntity);
		
		// Read the meta-data entity back and verify it
		List<ImageMetadataEntity> entities = imageService.findImageMetadata(jobInstanceId);
		Assert.assertEquals(1, entities.size());
		
		ImageMetadataEntity actualEntity = entities.get(0);
		Assert.assertEquals(pk, actualEntity.getPrimaryKey());
		Assert.assertEquals(titleId, actualEntity.getTitleId());
		Assert.assertEquals(width, actualEntity.getWidth());
		Assert.assertEquals(height, actualEntity.getHeight());
		Assert.assertEquals(size, actualEntity.getSize());
		Assert.assertEquals(dpi, actualEntity.getDpi());
		Assert.assertEquals(dimUnit, actualEntity.getDimUnits());
	}
}
