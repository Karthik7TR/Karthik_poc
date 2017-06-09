package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Arrays;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.TocGenerationStrategy;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider.TocGenerationStrategyProvider;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class ExtractTocStepTest
{
    private static final String FIRST_BUNDLE_FILE_NAME = "one.DIVXML.xml";
    private static final String SECOND_BUNDLE_FILE_NAME = "two.DIVXML.xml";

    @InjectMocks
    private ExtractTocStep step;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TocGenerationStrategyProvider tocGenerationStrategyProvider;
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

    @Before
    public void init()
    {
        given(bundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, SECOND_BUNDLE_FILE_NAME));
        given(chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES)
        ).willReturn(Arrays.asList(bundle));

        given(fileSystem.getBundlePartTocFile(eq(FIRST_BUNDLE_FILE_NAME), anyString(), eq(step))).willReturn(firstTocBundleFile);
        given(fileSystem.getBundlePartTocFile(eq(SECOND_BUNDLE_FILE_NAME), anyString(), eq(step))).willReturn(secondTocBundleFile);
        given(fileSystem.getTocFile(step)).willReturn(tocFile);
    }

    @Test
    public void shouldTransformOneFile() throws Exception
    {
        //given
        step.executeTransformation();

        verify(tocGenerationStrategyProvider, times(2)).getTocGenerationStrategy(BundleFileType.MAIN_CONTENT);

        final TocGenerationStrategy strategy = tocGenerationStrategyProvider
            .getTocGenerationStrategy(BundleFileType.MAIN_CONTENT);
        verify(strategy).performTocGeneration(FIRST_BUNDLE_FILE_NAME, bundle, step);
        verify(strategy).performTocGeneration(SECOND_BUNDLE_FILE_NAME, bundle, step);

        verify(transformationService)
            .transform(any(Transformer.class), eq(Arrays.asList(firstTocBundleFile, secondTocBundleFile)), eq(tocFile));
    }
}
