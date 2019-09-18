package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class FormatFileSystemImplTest {
    private static final String SEPARATOR = "/";
    private static final String WORK_DIRECTORY = "workDirectory";
    private static final String FORMAT_DIRECTORY = WORK_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_DIR.getName();
    private static final String SPLITBOOK_DIRECTORY = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_DIR.getName();
    private static final String SPLIT_NODE_INFO = SPLITBOOK_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE.getName();
    private static final String DOC_TO_IMAGE_MANIFEST = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_DOC_TO_IMAGE_MANIFEST_FILE.getName();
    private static final String FORMAT_HTML_WRAPPER_DIR = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR.getName();
    private static final String PREPROCESS_DIRECTORY = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_PREPROCESS_DIR.getName();
    private static final String TRANSFORMED_DIRECTORY = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_TRANSFORMED_DIR.getName();
    private static final String IMAGE_METADATA_DIRECTORY = FORMAT_DIRECTORY + SEPARATOR + NortTocCwbFileSystemConstants.FORMAT_IMAGE_METADATA_DIR.getName();

    @InjectMocks
    private FormatFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), WORK_DIRECTORY));
        given(bookFileSystem.getWorkDirectoryByJobId(anyLong())).willReturn(new File(temporaryFolder.getRoot(), WORK_DIRECTORY));
    }

    @Test
    public void shouldReturnFormatDirectory() {
        //given
        //when
        final File directory = fileSystem.getFormatDirectory(step);
        //then
        assertThat(directory, hasPath(FORMAT_DIRECTORY));
    }

    @Test
    public void shouldReturnImageMetadataDirectory() {
        //given
        //when
        final File directory = fileSystem.getImageMetadataDirectory(step);
        //then
        assertThat(directory, hasPath(IMAGE_METADATA_DIRECTORY));
    }

    @Test
    public void shouldReturnPreprocessDirectory() {
        //given
        //when
        final File directory = fileSystem.getPreprocessDirectory(step);
        //then
        assertThat(directory, hasPath(PREPROCESS_DIRECTORY));
    }


    @Test
    public void shouldReturnTransformedDirectory() {
        //given
        //when
        final File directory = fileSystem.getTransformedDirectory(step);
        //then
        assertThat(directory, hasPath(TRANSFORMED_DIRECTORY));
    }

    @Test
    public void shouldReturnHtmlWrapperDirectory() {
        //given
        //when
        final File directory = fileSystem.getHtmlWrapperDirectory(step);
        //then
        assertThat(directory, hasPath(FORMAT_HTML_WRAPPER_DIR));
    }

    @Test
    public void shouldReturnSplitBookDirectory() {
        //given
        //when
        final File directory = fileSystem.getSplitBookDirectory(step);
        //then
        assertThat(directory, hasPath(SPLITBOOK_DIRECTORY));
    }

    @Test
    public void shouldReturnSplitBookInfoFile() {
        //given
        //when
        final File file = fileSystem.getSplitBookInfoFile(step);
        //then
        assertThat(file, hasPath(SPLIT_NODE_INFO));
    }

    @Test
    public void shouldReturnImageToDocumentManifestFile() {
        //given
        //when
        final File file = fileSystem.getImageToDocumentManifestFile(step);
        //then
        assertThat(file, hasPath(DOC_TO_IMAGE_MANIFEST));
    }

    @Test
    public void shouldReturnFormatDirectoryByJobInstanceId() {
        //given
        //when
        final File directory = fileSystem.getFormatDirectory(1L);
        //then
        assertThat(directory, hasPath(FORMAT_DIRECTORY));
    }
}
