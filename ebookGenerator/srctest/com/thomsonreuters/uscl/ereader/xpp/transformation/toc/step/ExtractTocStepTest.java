package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class ExtractTocStepTest {
    private static final String FIRST_BUNDLE_FILE_NAME = "one.DIVXML.xml";
    private static final String SECOND_BUNDLE_FILE_NAME = "two.DIVXML.xml";
    private static final String MATERIAL_NUMBER = "123456";

    @InjectMocks
    private ExtractTocStep step;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppBundle bundle;
    @Mock
    private File firstTocBundleFile;
    @Mock
    private File secondTocBundleFile;
    @Mock
    private File tocFile;
    @Mock
    private File sourceFirstBundleFile;
    @Mock
    private File sourceSecondBundleFile;
    @Mock
    private File mergedTocFile;
    @Mock
    private File volumesMapFile;
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    @Before
    public void init() throws IOException {
        given(bundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, SECOND_BUNDLE_FILE_NAME));
        given(bundle.getMaterialNumber()).willReturn(MATERIAL_NUMBER);
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).willReturn(Arrays.asList(bundle));

        given(volumesMapFile.getAbsolutePath()).willReturn(StringUtils.EMPTY);

        given(
            fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, FIRST_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(sourceFirstBundleFile);
        given(
            fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, SECOND_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(sourceSecondBundleFile);
        given(fileSystem.getBundlePartTocFile(eq(FIRST_BUNDLE_FILE_NAME), anyString(), eq(step)))
            .willReturn(firstTocBundleFile);
        given(fileSystem.getBundlePartTocFile(eq(SECOND_BUNDLE_FILE_NAME), anyString(), eq(step)))
            .willReturn(secondTocBundleFile);
        given(fileSystem.getTocFile(step)).willReturn(tocFile);
        given(fileSystem.getMergedBundleTocFile(MATERIAL_NUMBER, step)).willReturn(mergedTocFile);
        given(fileSystem.getVolumesMapFile(step)).willReturn(volumesMapFile);

        given(mergedTocFile.createNewFile()).willReturn(true);
    }

    @Test
    public void shouldTransformOneFile() throws Exception {
        //given
        //when
        step.executeTransformation();
        //then
        then(transformationService).should(times(4)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();

        TransformationCommand command = iterator.next();
        assertThat(command.getInputFile(), is(sourceFirstBundleFile));
        assertThat(command.getOutputFile(), is(firstTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFile(), is(sourceSecondBundleFile));
        assertThat(command.getOutputFile(), is(secondTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(firstTocBundleFile, secondTocBundleFile));
        assertThat(command.getOutputFile(), is(mergedTocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(mergedTocFile));
        assertThat(command.getOutputFile(), is(tocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));
    }
}
