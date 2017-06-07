package com.thomsonreuters.uscl.ereader.xpp.unpackbundle;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileHierarchyMatcher.hasSameFileHierarchy;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.unpackbundle.step.UnpackBundleTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class UnpackBundleTaskTest
{
    private static final String MATERIAL_NUMBER = "123456";

    @InjectMocks
    @Resource(name = "unpackBundleTask")
    private UnpackBundleTask step;
    @Autowired
    private XppGatherFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Mock
    private XppBundleArchive xppBundleArchive;
    @Mock
    private XppBundleArchiveService xppBundleArchiveService;

    private File targetArchive;
    private File expectedUnpackedArchive;
    private File outputDirectory;

    @Before
    public void setUp() throws URISyntaxException
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        targetArchive =
            new File(UnpackBundleTaskTest.class.getResource("AJ2D_41963403_2017-04-17_04.07.22.610.-0400.zip").toURI());
        expectedUnpackedArchive = new File(UnpackBundleTaskTest.class.getResource("standard").toURI());
        outputDirectory = fileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER);

        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).willReturn(book);
    }

    @Test
    public void shouldUnpackBundle() throws Exception
    {
        //given
        given(book.getPrintComponents()).willReturn(printComponents());
        given(xppBundleArchiveService.findByMaterialNumber(MATERIAL_NUMBER)).willReturn(xppBundleArchive);
        given(xppBundleArchive.getEBookSrcFile()).willReturn(targetArchive);
        //when
        step.executeStep();
        //then
        assertThat(expectedUnpackedArchive, hasSameFileHierarchy(outputDirectory));
    }

    private Set<PrintComponent> printComponents()
    {
        final Set<PrintComponent> printComponents = new HashSet<>();
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setMaterialNumber(MATERIAL_NUMBER);
        printComponents.add(printComponent);
        return printComponents;
    }
}
