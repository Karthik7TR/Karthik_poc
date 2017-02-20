package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBookVersion;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenWorkDir;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.io.FileUtils;
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
public final class CreateDummyXppBookTest
{
    @InjectMocks
    private CreateDummyXppBook step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File xppStaticDirectory;
    private File workDir;

    @Before
    public void setUp() throws IOException, URISyntaxException
    {
        final File root = temporaryFolder.getRoot();
        xppStaticDirectory = new File(root, "xppStatic");
        xppStaticDirectory.mkdir();
        step.setXppStaticDirectory(xppStaticDirectory);

        final File title = new File(CreateDummyXppBookTest.class.getResource("title.xml").toURI());
        FileUtils.copyFileToDirectory(title, xppStaticDirectory);
        workDir = new File(root, "workDir");
    }

    @Test
    public void shouldCopyTemplateToAssembleDir() throws Exception
    {
        //given
        givenAll();
        //when
        step.executeStep();
        //then
        final File title = new File(workDir, "Assemble/title.xml");
        assertThat(title.exists(), is(true));
    }

    @Test
    public void shouldPrepareTitleXml() throws Exception
    {
        //given
        givenAll();
        //when
        step.executeStep();
        //then
        final File title = new File(workDir, "Assemble/title.xml");
        final String titleContent = FileUtils.readFileToString(title);
        assertThat(titleContent, containsString("titleversion=\"v1.1\""));
        assertThat(titleContent, containsString("id=\"id\""));
        assertThat(titleContent, containsString("<name>name</name>"));
    }

    private void givenAll()
    {
        givenWorkDir(chunkContext, workDir);
        givenBook(chunkContext, book);
        given(book.getFullyQualifiedTitleId()).willReturn("id");
        given(book.getProviewDisplayName()).willReturn("name");
        givenBookVersion(chunkContext, "v1.1");
    }
}
