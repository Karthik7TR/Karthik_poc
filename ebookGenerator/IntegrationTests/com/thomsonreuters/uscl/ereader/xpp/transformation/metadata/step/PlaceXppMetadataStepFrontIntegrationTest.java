package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("IntegrationTests")
@ContextConfiguration(classes = PlaceXppMetadataStepIntegrationTestConfig.class)
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
