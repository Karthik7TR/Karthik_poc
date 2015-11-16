package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

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
		novusFactory.setNovusEnvironment(NovusEnvironment.Prod);
		novusFactory.setProductName("EBOOKGENERATOR-USCL");

		novusImgService.setNovusFactory(novusFactory);

		NovusUtility novusUtil = new NovusUtility();
		novusUtil.setImgRetryCount("3");
		novusUtil.setShowMissDocsList("Y");
		novusImgService.setNovusUtility(novusUtil);
		
		
		
	}

	@Test
	public void testFetchImgsFromNovus() throws Exception {

		File workDir = temporaryFolder.getRoot();
		File dynamicImgDir = new File(workDir, "dynamicImgDir");
		dynamicImgDir.mkdirs();
		
		novusImgService.setMissingImageGuidsFileBasename("missing_image_guids.txt");

		imgDocGuidMap.put("DOCGUID1", "Ibf2635c0739211e585ec8cf559dfb9bd,Ibf287fb0739211e5863be83d2d715e2c,");
		imgDocGuidMap.put("DOCGUID2", "Ic274eb90739211e58459cc439435cf58");
		GatherResponse gatherResponse = novusImgService.fetchImages(imgDocGuidMap, dynamicImgDir, IS_FINAL_STAGE);
		assertTrue(gatherResponse.getNodeCount()==0);
		assertTrue(gatherResponse.getImageMetadataList().size() == 3);
		
		/*System.out.println(gatherResponse.getNodeCount());
		List<ImgMetadataInfo> imageMetadataInfoList = gatherResponse.getImageMetadataList();
		for (ImgMetadataInfo imgMetadataInfo : imageMetadataInfoList){
			System.out.println(imgMetadataInfo.toString());
		}*/

	}

}
