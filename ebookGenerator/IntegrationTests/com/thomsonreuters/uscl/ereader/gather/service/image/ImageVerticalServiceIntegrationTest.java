/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.service.image;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private static final Logger log = Logger.getLogger(ImageVerticalServiceIntegrationTest.class);
	private static final String GUID_TIF = "I5d463990094d11e085f5891ac64a9905";	// TIF image
	
//	    "Iead82f50a28811dbb436d78163d7301d",  // From bwray
//		"I449A045209354D19BADD202B264B3076",
//		"IA1F5243AA999498889F4D32E3D141970",
//		"IB813AED2574D4765839DD8196BBF692E",
//		"I3B6D30935B874190B99CE23DCD71F420",
//		"I8D6644A823A14778BFA4074B6D597D1D",
//		"IB815E4C168D7419AB24C4134C9E728D2" 

	
	FileOutputStream stream = null;
	Writer writer = null; 

	@Autowired ImageService imageService;

	@Test
	public void testFetchImageVerticalImage() {
//		File tmpImageDir = temporaryFolder.getRoot();
		File tmpImageDir = new File(System.getProperty("java.io.tmpdir")); // use this to save the image(s)
		long jobInstanceId = 1965;
		Map<String,String> DOC_IMAGE_GUID_MAP = new HashMap<String,String>();
		String titleId = "bogusTitleId";
		DOC_IMAGE_GUID_MAP.put("I8A302FE4920F47B00079B5381C71638B",	"123456789");
		DOC_IMAGE_GUID_MAP.put("I03a62830fca111e0961b0000837bc6dd",	"123456789");
		DOC_IMAGE_GUID_MAP.put("I5d463990094d11e085f5891ac64a9905",	"123456789");
		DOC_IMAGE_GUID_MAP.put("Ie043fac0675a11da90ebf04471783734",	"123456789");
		try {
			System.out.println("Writing files to: " + tmpImageDir);
			imageService.fetchImageVerticalImages(DOC_IMAGE_GUID_MAP, tmpImageDir, jobInstanceId, titleId);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			
		}
	}
	
	
//	@Test
	public void testFetchImageVerticalImageMetadata() {
		
		try {
			stream = new FileOutputStream(temporaryFolder + "_img_missing_guids.txt");
			writer = new OutputStreamWriter(stream, "UTF-8");
			SingleImageMetadataResponse response = imageService.fetchImageVerticalImageMetadata(GUID_TIF, writer, "999887789");
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();				
		}		
		} 
		
	
//	@Test
	public void testFetchImageMetadataBadGuid() {
		String badGuid = "IA31BCD5F18364C9BBDCD008012AFFFFF";
		try {
			stream = new FileOutputStream(temporaryFolder + "_img_missing_guids.txt");
			writer = new OutputStreamWriter(stream, "UTF-8");			
			SingleImageMetadataResponse response = imageService.fetchImageVerticalImageMetadata(badGuid, writer, "88787878787");
			Assert.assertNotNull(response);
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
//	@Test
	public void testPersistImageMetadata() {
		Long jobInstanceId = -999l;
		String titleId = "bogusTitleId";
		String guid = "someBogusTestingGuid";
		Long width = 100l, height = 200l, size = 300l, dpi = 400l;
		String dimUnit = "px";
		ImageMetadataEntityKey pk = new ImageMetadataEntityKey(jobInstanceId, guid);
		ImageMetadataEntity expectedEntity = new ImageMetadataEntity(pk, titleId,
																	 width, height, size, dpi, dimUnit, MediaType.IMAGE_PNG);
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
	
//	@Test
	public void testFindImageMetadataByPrimaryKey() throws Exception {
		try {
			long jobInstanceId = 1151;
			String imageGuid = "I7d5ecd90675f11da90ebf04471783734";
			ImageMetadataEntityKey key = new ImageMetadataEntityKey(jobInstanceId, imageGuid);
			ImageMetadataEntity entity = imageService.findImageMetadata(key);
			log.debug("Image metadata by PK: " + entity);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
