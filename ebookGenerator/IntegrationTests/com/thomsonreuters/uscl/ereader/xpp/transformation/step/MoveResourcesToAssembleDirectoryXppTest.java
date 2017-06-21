package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.DirectoryContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
@ContextConfiguration(locations = "./MoveResourcesToAssembleDirectoryXpp-context.xml")
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

    @Before
    public void setUp() throws Exception
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        mockBook();
        final File workDirectory = bookFileSystem.getWorkDirectory(sut);
        FileUtils.copyDirectory(new File(this.getClass().getResource("testdata").toURI()), workDirectory);
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
        final File expected = new File(this.getClass().getResource("expected").toURI());
        //when
        sut.executeStep();
        //then
        assertThat(expected, hasSameContentAs(titleDirectory));
    }

    private void mockBook()
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("bookDefn"))
            .willReturn(book);
        given(book.getTitleId()).willReturn("titleId");
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(1L);
    }
}
