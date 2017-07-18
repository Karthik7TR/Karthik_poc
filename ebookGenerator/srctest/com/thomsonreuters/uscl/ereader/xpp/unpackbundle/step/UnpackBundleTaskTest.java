package com.thomsonreuters.uscl.ereader.xpp.unpackbundle.step;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.service.compress.CompressService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
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
import org.springframework.batch.item.ExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public final class UnpackBundleTaskTest
{
    private static final String MATERIAL_NUMBER = "41963403";
    private File bundleXmlFile;

    @InjectMocks
    private UnpackBundleTask step;
    @Mock
    private XppGatherFileSystem xppGatherFileSystem;
    @Mock
    private CompressService zipService;
    @Mock
    private CompressService gzipService;
    @Mock
    private XppBundleArchiveService xppBundleArchiveService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private ExecutionContext executionContext;
    @Mock
    private BookDefinition book;
    @Mock
    private XppBundleArchive xppBundleArchive;
    @Mock
    private File archiveFile;
    @Mock
    private File destinationDirectoryFile;
    @Mock
    private File bundleDir;
    @Captor
    private ArgumentCaptor<List<XppBundle>> captor;

    @Before
    public void setUp() throws URISyntaxException
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext())
            .willReturn(executionContext);
        given(executionContext.get(JobParameterKey.EBOOK_DEFINITON)).willReturn(book);
        given(book.getPrintComponents()).willReturn(printComponents(2));
        given(xppBundleArchiveService.findByMaterialNumber(MATERIAL_NUMBER)).willReturn(xppBundleArchive);

        given(xppGatherFileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER))
            .willReturn(destinationDirectoryFile);
        given(xppGatherFileSystem.getXppBundlesDirectory(step)).willReturn(bundleDir);
        bundleXmlFile = new File(UnpackBundleTaskTest.class.getResource("bundle.xml").toURI());
        given(xppGatherFileSystem.getAllBundleXmls(step)).willReturn(Collections.singletonMap(MATERIAL_NUMBER, bundleXmlFile));
    }

    @Test
    public void shouldUnpackZipBundles() throws Exception
    {
        //given
        given(archiveFile.getName()).willReturn("archive.zip");
        given(xppBundleArchive.getEBookSrcFile()).willReturn(archiveFile);
        //when
        step.executeStep();
        //then
        verify(zipService, times(2)).decompress(archiveFile, destinationDirectoryFile);
    }

    @Test
    public void shouldUnpackTarGzBundles() throws Exception
    {
        //given
        given(archiveFile.getName()).willReturn("archive.tar.gz");
        given(xppBundleArchive.getEBookSrcFile()).willReturn(archiveFile);
        //when
        step.executeStep();
        //then
        verify(gzipService, times(2)).decompress(archiveFile, destinationDirectoryFile, "archive/");
    }

    @Test
    public void shouldUnmarshalBundleXmlFiles() throws Exception
    {
        //given
        given(archiveFile.getName()).willReturn("archive.zip");
        given(xppBundleArchive.getEBookSrcFile()).willReturn(archiveFile);
        given(book.getPrintComponents()).willReturn(printComponents(1));
        //when
        step.executeStep();
        //then
        then(executionContext).should().put(eq(JobParameterKey.XPP_BUNDLES), captor.capture());
        final XppBundle xppBundle = new XppBundle();
        xppBundle.setBundleRoot(
            "/apps/workflow-prod/data/phoenix_AJ2D_31908510//AJ2D_41963403_2017-04-17_04.07.22.610.-0400\n\t");
        xppBundle.setMaterialNumber(MATERIAL_NUMBER);
        xppBundle.setProductTitle("AM JUR 2D V6 REV 2017 PP IT-9");
        xppBundle.setProductType("supp");
        xppBundle.setReleaseDate(null);
        xppBundle.setVolumes(6);
        assertThat(captor.getValue(), contains(xppBundle));
    }

    private Set<PrintComponent> printComponents(final int size)
    {
        final Set<PrintComponent> printComponents = new HashSet<>();
        for (int i = 0; i < size; i++)
        {
            final PrintComponent printComponent = new PrintComponent();
            printComponent.setMaterialNumber(MATERIAL_NUMBER);
            printComponent.setComponentOrder(i + 1);
            printComponents.add(printComponent);
        }
        return printComponents;
    }
}
