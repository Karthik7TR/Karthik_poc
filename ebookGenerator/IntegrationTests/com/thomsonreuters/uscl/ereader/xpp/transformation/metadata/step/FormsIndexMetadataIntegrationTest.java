package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("IntegrationTests")
@ContextConfiguration(classes = PlaceXppMetadataStepIntegrationTestConfig.class)
public final class FormsIndexMetadataIntegrationTest extends PlaceXppMetadataStepFixture {
    public FormsIndexMetadataIntegrationTest()
        throws URISyntaxException {
        super("index/forms/1002-ETCD_Volume_1_form_Index.DIVXML.main",
            "index/forms/1002-ETCDExpected_Volume_1_form_Index.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToIndexFile() throws Exception {
        testPlacedMetadata();
    }

    @Override
    protected void testPlacedMetadata() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        final File actual = fileSystem.getStructureWithMetadataFile(step, VOL_MATERIAL_NUMBER, source.getName());
        assertThat(expected, hasSameContentAs(actual));
    }
}
