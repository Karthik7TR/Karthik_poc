package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.gather.img.util.TiffImageConverterImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

//TODO Re implement with new com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageService
public class NovusImageServiceTest
{
    private static final boolean IS_FINAL_STAGE = true;
    Map<String, String> imgDocGuidMap;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    NovusImgServiceImpl novusImgService;

    @Before
    public void setUp()
    {
        // The object under test
        novusImgService = new NovusImgServiceImpl();
        imgDocGuidMap = new HashMap<String, String>();
        final NovusFactoryImpl novusFactory = new NovusFactoryImpl();
        novusFactory.setBusinessUnit("WestCobalt");
        novusFactory.setNovusEnvironment(NovusEnvironment.Client);
        novusFactory.setProductName("EBOOKGENERATOR-USCL");

        novusImgService.setNovusFactory(novusFactory);

        final NovusUtility novusUtil = new NovusUtility();
        novusUtil.setImgRetryCount("3");
        novusUtil.setShowMissDocsList("Y");
        novusImgService.setNovusUtility(novusUtil);

        final TiffImageConverterImpl imageConverter = new TiffImageConverterImpl();
        imageConverter.init();
        imageConverter.setTiffReaderClass("it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader");
        novusImgService.setImageConverter(imageConverter);
    }

    @Test
    public void testFetchImgsFromNovus() throws Exception
    {
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
