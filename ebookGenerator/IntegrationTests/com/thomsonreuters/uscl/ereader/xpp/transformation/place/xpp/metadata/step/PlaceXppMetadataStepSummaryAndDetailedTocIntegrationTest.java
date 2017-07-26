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
public final class PlaceXppMetadataStepSummaryAndDetailedTocIntegrationTest extends PlaceXppMetadataStepFixture
{
    public PlaceXppMetadataStepSummaryAndDetailedTocIntegrationTest()
        throws URISyntaxException
    {
        super("sum/det/toc/20002-volume_2_Summary_and_Detailed_Table_of_Contents.DIVXML.main",
            "sum/det/toc/Expected20002-volume_2_Summary_and_Detailed_Table_of_Contents.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception
    {
        testPlacedMetadata();
    }
}
