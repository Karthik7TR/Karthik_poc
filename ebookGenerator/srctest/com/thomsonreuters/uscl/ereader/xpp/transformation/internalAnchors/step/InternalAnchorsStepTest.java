package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
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
public final class InternalAnchorsStepTest {
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String MATERIAL_NUMBER_2 = "11111112";

    @InjectMocks
    private InternalAnchorsStep step;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock
    private XslTransformationService transformationService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private XppBundle boundBundle;
    @Mock
    private XppBundle suppBundle;
    @Mock
    private BookDefinition bookDefinition;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File mapFile;
    private File boundFile;
    private File suppFile;

    @Before
    public void setUp() throws IOException {
        given(bookDefinition.getPrintComponents()).willReturn(getPrintComponents());

        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(Arrays.asList(boundBundle, suppBundle));
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).willReturn(bookDefinition);

        given(boundBundle.getProductType()).willReturn("bound");
        given(boundBundle.getMaterialNumber()).willReturn(MATERIAL_NUMBER);
        given(suppBundle.getProductType()).willReturn("supp");
        given(suppBundle.isPocketPartPublication()).willReturn(true);
        given(suppBundle.getMaterialNumber()).willReturn(MATERIAL_NUMBER_2);
        final File htmlPagesDir = mock(File.class);
        final File htmlPagesDir2 = mock(File.class);
        final Map<String, Collection<File>> files = new HashMap<>();
        files.put(MATERIAL_NUMBER, Arrays.asList(htmlPagesDir));
        files.put(MATERIAL_NUMBER_2, Arrays.asList(htmlPagesDir2));

        given(htmlPagesDir.listFiles(any(FileFilter.class))).willReturn(new File[] {});
        given(htmlPagesDir.listFiles()).willReturn(new File[] {});
        given(htmlPagesDir2.listFiles(any(FileFilter.class))).willReturn(new File[] {});
        given(htmlPagesDir2.listFiles()).willReturn(new File[] {});
        given(fileSystem.getFiles(step, XppFormatFileSystemDir.SECTIONBREAKS_DIR)).willReturn(files);

        final TransformerBuilder builder = mock(TransformerBuilder.class);
        final Transformer transformer = mock(Transformer.class);
        given(transformerBuilderFactory.create()).willReturn(builder);
        given(builder.withXsl(any(File.class))).willReturn(builder);
        given(builder.withParameter(anyString(), anyBoolean())).willReturn(builder);
        given(builder.build()).willReturn(transformer);

        final File root = temporaryFolder.getRoot();
        mapFile = new File("mapFile");
        boundFile = new File(root, "boundFile");
        suppFile = new File(root, "suppFile");
        Files.createFile(boundFile.toPath());
        Files.createFile(suppFile.toPath());
        given(fileSystem.getAnchorToDocumentIdMapFile(step)).willReturn(mapFile);
        given(fileSystem.getAnchorToDocumentIdMapFile(step, MATERIAL_NUMBER)).willReturn(mapFile);
        given(fileSystem.getAnchorToDocumentIdMapFile(step, MATERIAL_NUMBER_2)).willReturn(mapFile);
        given(fileSystem.getAnchorToDocumentIdMapBoundFile(step, MATERIAL_NUMBER)).willReturn(boundFile);
        given(fileSystem.getAnchorToDocumentIdMapBoundFile(step, MATERIAL_NUMBER_2)).willReturn(boundFile);
    }

    private Set<PrintComponent> getPrintComponents() {
        final PrintComponent firstPrintComponent = new PrintComponent();
        firstPrintComponent.setSplitter(false);
        firstPrintComponent.setComponentOrder(1);
        firstPrintComponent.setMaterialNumber(MATERIAL_NUMBER);

        final PrintComponent secondPrintComponent = new PrintComponent();
        secondPrintComponent.setSplitter(false);
        secondPrintComponent.setComponentOrder(2);
        secondPrintComponent.setMaterialNumber(MATERIAL_NUMBER_2);

        return Stream.of(firstPrintComponent, secondPrintComponent).collect(Collectors.toCollection(HashSet::new));
    }

    @Test
    public void shouldTransform() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(transformationService).should(times(5)).transform((TransformationCommand) any());
        then(fileSystem).should().getFiles(eq(step), eq(XppFormatFileSystemDir.SECTIONBREAKS_DIR));
        then(fileSystem).should().getAnchorToDocumentIdMapFile(eq(step), eq(MATERIAL_NUMBER));
        then(fileSystem).should().getAnchorToDocumentIdMapFile(eq(step), eq(MATERIAL_NUMBER_2));
        then(fileSystem).should().getAnchorToDocumentIdMapBoundFile(eq(step), eq(MATERIAL_NUMBER));
        then(fileSystem).should().getAnchorToDocumentIdMapBoundFile(eq(step), eq(MATERIAL_NUMBER_2));
    }
}
