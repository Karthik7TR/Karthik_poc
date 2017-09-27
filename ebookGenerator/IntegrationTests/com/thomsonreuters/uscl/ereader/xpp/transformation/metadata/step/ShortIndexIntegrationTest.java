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
public final class ShortIndexIntegrationTest extends PlaceXppMetadataStepFixture
{

    public ShortIndexIntegrationTest()
        throws URISyntaxException
    {
        super("shortIndex/1001-C_V_3_Index.DIVXML.main", "shortIndex/expected.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToIndexFile() throws Exception
    {
        testPlacedMetadata();
    }
}
