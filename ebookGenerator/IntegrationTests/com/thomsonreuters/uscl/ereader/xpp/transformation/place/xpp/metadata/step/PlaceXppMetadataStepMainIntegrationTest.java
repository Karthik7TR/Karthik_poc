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
public final class PlaceXppMetadataStepMainIntegrationTest extends PlaceXppMetadataStepFixture
{
    public PlaceXppMetadataStepMainIntegrationTest()
        throws URISyntaxException
    {
        super("main/1-CHALSource.DIVXML.main", "main/1-CHALExpected.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception
    {
        testPlacedMetadata();
    }
}
