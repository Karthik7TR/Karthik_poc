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

import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
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
	public void testFetchImage() {
		File tmpImageDir = new File (System.getProperty("java.io.tmpdir"));
		long jobInstanceId = 1965;
		List<String> imageGuids = new ArrayList<String>(1);
		imageGuids.add(GUID);
		try {
			imageService.fetchImages(imageGuids, tmpImageDir, jobInstanceId);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			
		}
	}
}
