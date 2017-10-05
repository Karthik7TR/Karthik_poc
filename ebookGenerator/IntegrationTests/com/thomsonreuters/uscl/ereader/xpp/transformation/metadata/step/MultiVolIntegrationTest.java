package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("IntegrationTests")
@ContextConfiguration(classes = PlaceXppMetadataStepIntegrationTestConfig.class)
public final class MultiVolIntegrationTest extends PlaceXppMetadataStepFixture {
    public MultiVolIntegrationTest()
        throws URISyntaxException {
        super("multivol/0-CHALSource_Front_vol_1.DIVXML.main", "multivol/0-CHALExpected_Front_vol_1.DIVXML.main");
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception {
        testPlacedMetadata();
    }

    @Override
    protected List<XppBundle> getBundlesList() {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("0-CHALSource_Front_vol_1.DIVXML.main"));
        return Arrays.asList(volumeOneBundle, volumeOneBundle);
    }
}
