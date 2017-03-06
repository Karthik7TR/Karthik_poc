package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBookVersion;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
    @Mock
    private AssembleFileSystem fileSystem;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File xppStaticDirectory;
    private File titleDir;
    private File titleXml;

    @Before
    public void setUp() throws IOException, URISyntaxException, IllegalAccessException
    {
        final File root = temporaryFolder.getRoot();
        xppStaticDirectory = new File(root, "xppStatic");
        xppStaticDirectory.mkdir();
        FieldUtils.writeField(step, "xppStaticDirectory", xppStaticDirectory, true);

        final File title = new File(CreateDummyXppBookTest.class.getResource("title.xml").toURI());
        FileUtils.copyFileToDirectory(title, xppStaticDirectory);
        titleDir = new File(root, "titleDir");
        titleXml = new File(titleDir, "title.xml");

        given(fileSystem.getTitleDirectory(step)).willReturn(titleDir);
        given(fileSystem.getTitleXml(step)).willReturn(titleXml);
    }

    @Test
    public void shouldCopyTemplateToAssembleDir() throws Exception
    {
        //given
        givenAll();
        //when
        step.executeStep();
        //then
        assertThat(titleXml.exists(), is(true));
    }

    @Test
    public void shouldPrepareTitleXml() throws Exception
    {
        //given
        givenAll();
        //when
        step.executeStep();
        //then
        final String titleContent = FileUtils.readFileToString(titleXml);
        assertThat(titleContent, containsString("titleversion=\"v1.1\""));
        assertThat(titleContent, containsString("id=\"id\""));
        assertThat(titleContent, containsString("<name>name</name>"));
    }

    private void givenAll()
    {
        givenBook(chunkContext, book);
        given(book.getTitleId()).willReturn("titleId");
        given(book.getFullyQualifiedTitleId()).willReturn("id");
        given(book.getProviewDisplayName()).willReturn("name");
        givenBookVersion(chunkContext, "1.1");
    }
}
