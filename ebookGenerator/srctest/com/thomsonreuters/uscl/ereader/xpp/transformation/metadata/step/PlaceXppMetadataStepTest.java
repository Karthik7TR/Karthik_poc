package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy.PlaceXppMetadataStrategyProviderImpl;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PlaceXppMetadataStepTest {
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private PlaceXppMetadataStep step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PlaceXppMetadataStrategyProviderImpl strategyProvider;

    @Mock
    private File bundleDirectory;
    @Mock
    private File frontFile;

    @Before
    public void onTestSetUp() {
        given(frontFile.getName()).willReturn("MAIN_Front_vol_1.DIVXML.main");

        given(fileSystem.getOriginalMainAndFootnoteFiles(step)).willReturn(getFiles());
        given(fileSystem.getStructureWithMetadataBundleDirectory(step, MATERIAL_NUMBER)).willReturn(bundleDirectory);
        given(
            fileSystem
                .getStructureWithMetadataFile(Matchers.eq(step), Matchers.eq(MATERIAL_NUMBER), Matchers.anyString()))
                    .willReturn(bundleDirectory);
    }

    private Map<String, Collection<File>> getFiles() {
        final Map<String, Collection<File>> files = new HashMap<>();
        files.put(MATERIAL_NUMBER, Arrays.asList(frontFile));
        return files;
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();

        //then
        then(strategyProvider.getStrategy(BundleFileType.FRONT)).should()
            .performHandling(frontFile, MATERIAL_NUMBER, step);
    }
}
