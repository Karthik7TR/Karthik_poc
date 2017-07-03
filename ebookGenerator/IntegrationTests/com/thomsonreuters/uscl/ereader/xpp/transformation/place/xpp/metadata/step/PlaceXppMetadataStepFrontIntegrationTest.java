package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("PlaceXppMetadataStepIntegrationTest-context.xml")
public final class PlaceXppMetadataStepFrontIntegrationTest extends PlaceXppMetadataStepFixture
{
    public PlaceXppMetadataStepFrontIntegrationTest()
        throws URISyntaxException
    {
        super("front/0-CHALSource_Front_vol_1.DIVXML.main", "front/0-CHALExpected_Front_vol_1.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception
    {
        testPlacedMetadata();
    }
}
