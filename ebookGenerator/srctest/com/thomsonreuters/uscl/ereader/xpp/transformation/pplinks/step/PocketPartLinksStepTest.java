package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.DocumentFile;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public class PocketPartLinksStepTest {
    private static final String DIVXML_XML_MAIN = "1-LUPDRL.DIVXML_0002_I91dd17d0572311dca3950000837bc6dd.page";
    private static final String DIVXML_XML_SUPP = "1-LUPDRL.DIVXML_0001_I91dd17d0572311dca3950000837bc6dd.page";
    private static final String UUID = "I91dd17d0572311dca3950000837bc6dd";

    private static final String MATERIAL_NUMBER_MAIN = "11111111";
    private static final String MATERIAL_NUMBER_SUPP = "11111112";

    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @InjectMocks
    private PocketPartLinksStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private TransformerBuilder transformerBuilder;

    @Before
    public void setUp() throws IOException {
        final File root = temporaryFolder.getRoot();

        final File originalPagesDirMain = mkdir(root, SOURCE_DIR.getDirName(), MATERIAL_NUMBER_MAIN);
        final File originalPagesDirSupp = mkdir(root, SOURCE_DIR.getDirName(), MATERIAL_NUMBER_SUPP);
        final File originalFileMain = mkfile(originalPagesDirMain, DIVXML_XML_MAIN);
        final File originalFileSupp = mkfile(originalPagesDirSupp, DIVXML_XML_SUPP);

        given(fileSystem.getFiles(step, SOURCE_DIR)).willReturn(getFiles(originalFileMain, originalFileSupp));

        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(getXppBundles());

        final File destDirectory = mkdir(root, DESTINATION_DIR.getDirName(), MATERIAL_NUMBER_MAIN);
        given(fileSystem.getDirectory(any(), any(), any())).willReturn(destDirectory);
        given(fileSystem.getFile(any(), any(), any(), any())).willReturn(new File(destDirectory, "temp"));

        given(transformerBuilderFactory.create()).willReturn(transformerBuilder);
        given(transformerBuilder.withXsl(any(File.class))).willReturn(transformerBuilder);
        given(transformerBuilder.withParameter(any(String.class), any())).willReturn(transformerBuilder);
        given(transformerBuilder.build()).willReturn(mock(Transformer.class));
    }

    private Map<String, Collection<File>> getFiles(final File main, final File supp) {
        final Map<String, Collection<File>> map = new HashMap<>();
        map.put(MATERIAL_NUMBER_MAIN, Arrays.asList(main));
        map.put(MATERIAL_NUMBER_SUPP, Arrays.asList(supp));
        return map;
    }

    private List<XppBundle> getXppBundles() {
        final XppBundle mainBundle = new XppBundle();
        mainBundle.setMaterialNumber(MATERIAL_NUMBER_MAIN);
        mainBundle.setOrderedFileList(Arrays.asList(DIVXML_XML_MAIN));

        final XppBundle suppBundle = new XppBundle();
        suppBundle.setMaterialNumber(MATERIAL_NUMBER_SUPP);
        suppBundle.setOrderedFileList(Arrays.asList(DIVXML_XML_SUPP));
        suppBundle.setProductType("supp");
        return Arrays.asList(suppBundle);
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should(times(2)).transform(any(TransformationCommand.class));
        then(transformerBuilder).should().withParameter(eq("isPocketPart"), eq(true));
        then(transformerBuilder).should().withParameter(eq("isPocketPart"), eq(false));
        then(transformerBuilder).should(times(2)).withParameter(eq("docId"), eq(UUID));
    }

    @Test
    public void shouldCreateIsPocketPartToUuidToFileNameMap() {
        final Map<Boolean, Map<String, DocumentFile>> map = step.getIsPocketPartToUuidToFileNameMap(getXppBundles());
        assertEquals(map.get(true).size(), 1);
        assertEquals(map.get(false).size(), 1);
        assertEquals(map.get(true).get(UUID).getFile().getName(), DIVXML_XML_SUPP);
        assertEquals(map.get(false).get(UUID).getFile().getName(), DIVXML_XML_MAIN);
    }
}
