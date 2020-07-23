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
public final class TableOfCasesXppMetadataIntegrationTest extends PlaceXppMetadataStepFixture {
    public TableOfCasesXppMetadataIntegrationTest()
        throws URISyntaxException {
        super(
            "tableofcases/30008-volume_3_Table_of_Cases.DIVXML.main",
            "tableofcases/Expected30008-volume_3_Table_of_Cases.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToTableOfCasesFile() throws Exception {
        testPlacedMetadata();
    }
}