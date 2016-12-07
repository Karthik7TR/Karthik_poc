/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.gather.img.controller.ImgControllerTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageFinderImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageProcessorImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageServiceImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtilImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageMetadataHandlerTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.NovusImageMetadataParserImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.TiffImageConverterImplTest;

@RunWith(Suite.class)
@SuiteClasses({ NovusImageFinderImplTest.class, NovusImageServiceImplTest.class, DocToImageManifestUtilImplTest.class,
		ImageMetadataHandlerTest.class, NovusImageMetadataParserImplTest.class, NovusImageProcessorImplTest.class,
		ImgControllerTest.class, TiffImageConverterImplTest.class })
public class ImagesTestSuite {
}
