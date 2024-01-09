package com.thomsonreuters.uscl.ereader.common.service.splitnode;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SplitNodesInfoServiceImplTest {
    @InjectMocks
    private SplitNodesInfoServiceImpl service;
    @Mock
    private BookDefinition book;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File splitNodeInfoFile;
    private File incorrectFile;

    @Before
    public void setUp() throws URISyntaxException {
        splitNodeInfoFile = new File(SplitNodesInfoServiceImplTest.class.getResource("splitNodeInfo.txt").toURI());
        incorrectFile = new File("splitNodeInfoIncorrect.txt");
    }

    @Test
    public void shouldReturnListOfTitles() {
        //given
        final String fullyQualifiedTitleId = "uscl/an/split_splitpro";
        //when
        final List<String> list = service.getTitleIds(splitNodeInfoFile, fullyQualifiedTitleId);
        //then
        assertThat(list, contains("uscl/an/split_splitpro", "uscl/an/split_splitpro_pt2"));
    }

    @Test
    public void shouldThrowExceptionIfFileNotFound() {
        //given
        thrown.expect(RuntimeException.class);
        final String fullyQualifiedTitleId = "uscl/an/split_splitpro";
        //when
        service.getTitleIds(incorrectFile, fullyQualifiedTitleId);
        //then
    }

    @Test
    public void shouldReturnSubmittedSplitNodes() {
        final Version submittedVersion = version("v1.1");
        //given
        //when
        final Set<SplitNodeInfo> splitNodes = service.getSubmittedSplitNodes(splitNodeInfoFile, book, submittedVersion);
        //then
        assertThat(
            splitNodes,
            contains(splitNode(book, "uscl/an/split_splitpro_pt2", "N7EC6E200EB9211DF93C89FD5FF47AAB5", "1.1")));
    }
}
