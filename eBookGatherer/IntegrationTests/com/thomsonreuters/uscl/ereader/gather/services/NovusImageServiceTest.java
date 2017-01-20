/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.img.util.TiffImageConverterImpl;

//TODO reimplement with new com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageService
public class NovusImageServiceTest {

	private static final boolean IS_FINAL_STAGE = true;
	Map<String, String> imgDocGuidMap;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	NovusImgServiceImpl novusImgService;

	@Before
	public void setUp() {
		// The object under test
		this.novusImgService = new NovusImgServiceImpl();
		imgDocGuidMap = new HashMap<String, String>();
		NovusFactoryImpl novusFactory = new NovusFactoryImpl();
		novusFactory.setBusinessUnit("WestCobalt");
		novusFactory.setNovusEnvironment(NovusEnvironment.Client);
		novusFactory.setProductName("EBOOKGENERATOR-USCL");

		novusImgService.setNovusFactory(novusFactory);

		NovusUtility novusUtil = new NovusUtility();
		novusUtil.setImgRetryCount("3");
		novusUtil.setShowMissDocsList("Y");
		novusImgService.setNovusUtility(novusUtil);
		
		TiffImageConverterImpl imageConverter = new TiffImageConverterImpl();
		imageConverter.init();
		imageConverter.setTiffReaderClass("it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader");
		novusImgService.setImageConverter(imageConverter);
	}

	@Test
	public void testFetchImgsFromNovus() throws Exception {
		/*
		File workDir = temporaryFolder.getRoot();
		File dynamicImgDir = new File(workDir, "dynamicImgDir");
		dynamicImgDir.mkdirs();
		
		novusImgService.setMissingImageGuidsFileBasename("missing_image_guids.txt");

		imgDocGuidMap.put("DOCGUID1", "Ibf2635c0739211e585ec8cf559dfb9bd,Ibf287fb0739211e5863be83d2d715e2c,");
		imgDocGuidMap.put("DOCGUID2", "I5d463990094d11e085f5891ac64a9905");
		GatherResponse gatherResponse = novusImgService.fetchImages(imgDocGuidMap, dynamicImgDir, IS_FINAL_STAGE);
		assertTrue(gatherResponse.getMissingImgCount() == 2);
		assertTrue(gatherResponse.getImageMetadataList().size() == 1);
 		*/	
		/*System.out.println(gatherResponse.getNodeCount());
		List<ImgMetadataInfo> imageMetadataInfoList = gatherResponse.getImageMetadataList();
		for (ImgMetadataInfo imgMetadataInfo : imageMetadataInfoList){
			System.out.println(imgMetadataInfo.toString());
		}*/
	}
	

}
