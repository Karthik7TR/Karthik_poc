package com.thomsonreuters.uscl.ereader.gather.img;

import com.thomsonreuters.uscl.ereader.gather.img.controller.ImgControllerTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageFinderImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageProcessorImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageServiceImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtilImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageMetadataHandlerTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.NovusImageMetadataParserImplTest;
import com.thomsonreuters.uscl.ereader.gather.img.util.TiffImageConverterImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    NovusImageFinderImplTest.class,
    NovusImageServiceImplTest.class,
    DocToImageManifestUtilImplTest.class,
    ImageMetadataHandlerTest.class,
    NovusImageMetadataParserImplTest.class,
    NovusImageProcessorImplTest.class,
    ImgControllerTest.class,
    TiffImageConverterImplTest.class})
public class ImagesTestSuite
{
    //Intentionally left blank
}
