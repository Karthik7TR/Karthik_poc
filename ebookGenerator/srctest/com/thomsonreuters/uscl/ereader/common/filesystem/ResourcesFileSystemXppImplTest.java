package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.File;

import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.common.filesystem.exception.StylesheetsNotFoundException;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ResourcesFileSystemXppImplTest {
    @InjectMocks
    private ResourcesFileSystemXppImpl sut;
    @Mock
    private XppFormatFileSystemImpl xppFormatFileSystemImpl;
    @Mock
    private ImageFileSystemImpl imageFileSystemImpl;
    @Mock
    private CoverArtUtil coverArtUtil;
    @Mock
    private BookStep step;
    @Mock
    private File fakeDir;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private static final String DIR_NAME = XppFormatFileSystemDir.UNESCAPE_DIR.getDirName();

    @Test
    public void shouldReturnDocumentsDirectory() {
        //given
        given(xppFormatFileSystemImpl.getDirectory(step, XppFormatFileSystemDir.UNESCAPE_DIR)).willReturn(fakeDir);
        given(fakeDir.getAbsolutePath()).willReturn("workDirectory/Format/" + DIR_NAME);
        //when
        final File file = sut.getDocumentsDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Format/" + DIR_NAME));
    }

    @Test
    @SneakyThrows
    public void shouldReturnDocumentCss() {
        //given
        final File[] files = {new File("X_document_1.2.3.css"), new File("document_1.2.3.css")};
        FieldUtils.writeField(sut, "stylesheetDir", fakeDir, true);
        given(fakeDir.listFiles()).willReturn(files);
        //when
        final File file = sut.getDocumentCss();
        //then
        assertEquals(file.getName(), "document_1.2.3.css");
    }

    @Test(expected = StylesheetsNotFoundException.class)
    @SneakyThrows
    public void shouldThrowIfDocumentCssNotFound() {
        //given
        final File[] files = {new File("X_document_1.2.3.css")};
        FieldUtils.writeField(sut, "stylesheetDir", fakeDir, true);
        given(fakeDir.listFiles()).willReturn(files);
        //when
        sut.getDocumentCss();
    }

    @Test
    public void shouldReturnAssetsDirectory() {
        //given
        given(imageFileSystemImpl.getImageDynamicDirectory(step))
            .willReturn(new File(temporaryFolder.getRoot(), "workDirectory/Gather/Images/Dynamic"));
        //when
        final File file = sut.getAssetsDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Gather/Images/Dynamic"));
    }

    @Test
    public void shouldReturnArtwork() {
        //given
        given(coverArtUtil.getCoverArt(any(BookDefinition.class))).willReturn(new File("coverArt.PNG"));
        //when
        final File file = sut.getArtwork(step);
        //then
        assertThat(file, hasPath("coverArt.PNG"));
    }

    @Test
    public void shouldReturnFontsCssFiles() {
        //given
        given(fakeDir.isDirectory()).willReturn(true);
        given(xppFormatFileSystemImpl.getFontsCssDirectory(step)).willReturn(fakeDir);
        //when
        sut.getFontsCssFiles(step);
        //then
        verify(xppFormatFileSystemImpl).getFontsCssDirectory(step);
    }
}
