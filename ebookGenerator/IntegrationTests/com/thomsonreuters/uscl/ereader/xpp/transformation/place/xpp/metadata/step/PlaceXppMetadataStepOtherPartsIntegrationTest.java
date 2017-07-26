package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("IntegrationTests")
@ContextConfiguration(classes = PlaceXppMetadataStepIntegrationTestConfig.class)
public final class PlaceXppMetadataStepOtherPartsIntegrationTest extends PlaceXppMetadataStepFixture
{
    public PlaceXppMetadataStepOtherPartsIntegrationTest()
        throws URISyntaxException
    {
        super("otherparts/30002Source-volume_3_Detailed_Table_of_Contents.DIVXML.main", "otherparts/30002Expected-volume_3_Detailed_Table_of_Contents.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception
    {
        testPlacedMetadata();
    }
}
