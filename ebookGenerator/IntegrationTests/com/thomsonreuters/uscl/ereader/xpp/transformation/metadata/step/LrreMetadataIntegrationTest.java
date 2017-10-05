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
public final class LrreMetadataIntegrationTest extends PlaceXppMetadataStepFixture {
    public LrreMetadataIntegrationTest()
        throws URISyntaxException {
        super("lrre/30007-volume_3_Table_of_LRRE.DIVXML.main", "lrre/expected.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToLrreFile() throws Exception {
        testPlacedMetadata();
    }
}
