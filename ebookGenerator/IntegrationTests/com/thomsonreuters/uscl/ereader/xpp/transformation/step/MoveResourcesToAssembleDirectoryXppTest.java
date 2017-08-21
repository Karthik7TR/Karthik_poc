package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.DirectoryContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ResourcesFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ResourcesFileSystemXppImpl;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.xpp.transformation.move.resources.step.MoveResourcesToAssembleDirectoryXpp;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class MoveResourcesToAssembleDirectoryXppTest
{
    @Resource(name = "moveResourcesToAssembleDirectoryXppTask")
    @InjectMocks
    private MoveResourcesToAssembleDirectoryXpp sut;
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Value("${xpp.document.css}")
    private File documentCssFile;

    private File expectedDir;

    @Before
    public void setUp() throws Exception
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        mockBook();
        expectedDir = new File(this.getClass().getResource("expected").toURI());
        final File workDirectory = bookFileSystem.getWorkDirectory(sut);
        FileUtils.copyDirectory(new File(this.getClass().getResource("testdata").toURI()), workDirectory);
        final File assetsDir = new File(expectedDir, "assets");
        FileUtils.copyFileToDirectory(documentCssFile, assetsDir);
    }

    @After
    public void tearDown() throws Exception
    {
        final File workingDir = bookFileSystem.getWorkDirectory(sut);
        FileUtils.deleteDirectory(workingDir);
    }

    @Test
    public void test() throws Exception
    {
        //given
        final File assembleDirectory = assembleFileSystem.getAssembleDirectory(sut);
        final File titleDirectory = new File(assembleDirectory, "titleId");
        //when
        sut.executeStep();
        //then
        assertThat(expectedDir, hasSameContentAs(titleDirectory, true));
    }

    private void mockBook()
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("bookDefn"))
            .willReturn(book);
        given(book.getTitleId()).willReturn("titleId");
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(1L);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class MoveResourcesToAssembleDirectoryXppTestConfiguration
    {
        @Bean(name = "moveResourcesToAssembleDirectoryXppTask")
        public MoveResourcesToAssembleDirectoryXpp moveResourcesToAssembleDirectoryXppTask()
        {
            return new MoveResourcesToAssembleDirectoryXpp();
        }

        @Bean
        public ResourcesFileSystem resourcesFileSystemXpp()
        {
            return new ResourcesFileSystemXppImpl();
        }
    }
}
