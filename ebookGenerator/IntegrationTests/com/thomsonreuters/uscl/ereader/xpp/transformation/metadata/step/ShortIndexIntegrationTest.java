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
public final class ShortIndexIntegrationTest extends PlaceXppMetadataStepFixture
{

    public ShortIndexIntegrationTest()
        throws URISyntaxException
    {
        super("shortIndex/1001-C_V_3_Index.DIVXML.main", "shortIndex/expected_IndexBreaks.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToIndexFile() throws Exception
    {
        testPlacedMetadata();
    }

    @Override
    protected void testPlacedMetadata() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        final File actual = fileSystem
            .getIndexBreaksFile(step, VOL_MATERIAL_NUMBER, "indexBreaks-" + source.getName());
        assertThat(expected, hasSameContentAs(actual));
    }
}
