package com.thomsonreuters.uscl.ereader.xpp.unpackbundle;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileHierarchyMatcher.hasSameFileHierarchy;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDaoImpl;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystemImpl;
import com.thomsonreuters.uscl.ereader.xpp.unpackbundle.step.UnpackBundleTask;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class UnpackBundleIntegrationTest
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
        expectedUnpackedArchive = new File(UnpackBundleIntegrationTest.class.getResource("standard").toURI());
        outputDirectory = fileSystem.getXppBundleMaterialNumberDirectory(step, MATERIAL_NUMBER);

        given(book.getPrintComponents()).willReturn(printComponents());
        given(xppBundleArchiveService.findByMaterialNumber(MATERIAL_NUMBER)).willReturn(xppBundleArchive);
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).willReturn(book);
    }

    @Test
    public void shouldUnpackZipBundle() throws Exception
    {
        //given
        targetArchive =
            new File(UnpackBundleIntegrationTest.class.getResource("AJ2D_41963403_2017-04-17_04.07.22.610.-0400.zip").toURI());
        given(xppBundleArchive.getEBookSrcFile()).willReturn(targetArchive);
        //when
        step.executeStep();
        //then
        assertThat(expectedUnpackedArchive, hasSameFileHierarchy(outputDirectory));
    }

    @Test
    public void shouldUnpackTarGzBundle() throws Exception
    {
        //given
        targetArchive =
            new File(UnpackBundleIntegrationTest.class.getResource("AJ2D_41963403_2017-04-17_04.07.22.610.-0400.tar.gz").toURI());
        given(xppBundleArchive.getEBookSrcFile()).willReturn(targetArchive);
        //when
        step.executeStep();
        //then
        assertThat(expectedUnpackedArchive, hasSameFileHierarchy(outputDirectory));
    }

    @After
    public void onTestComplete() throws IOException
    {
        FileUtils.cleanDirectory(outputDirectory);
    }

    private Set<PrintComponent> printComponents()
    {
        final Set<PrintComponent> printComponents = new HashSet<>();
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setMaterialNumber(MATERIAL_NUMBER);
        printComponents.add(printComponent);
        return printComponents;
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class UnpackBundleIntegrationTestConfiguration
    {
        @Bean
        public UnpackBundleTask unpackBundleTask()
        {
            return new UnpackBundleTask();
        }

        @Bean
        public GatherFileSystem xppGatherFileSystem()
        {
            return new XppGatherFileSystemImpl();
        }

        @Bean
        public XppBundleArchiveService xppBundleArchiveService()
        {
            return new XppBundleArchiveService();
        }

        @Bean
        public XppBundleArchiveDao xppBundleArchiveDao()
        {
            return new XppBundleArchiveDaoImpl(null);
        }
    }
}
