package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.io.File;

import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ResourcesFileSystemXppImplTest
{
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
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Test method for {@link com.thomsonreuters.uscl.ereader.common.filesystem.ResourcesFileSystemXppImpl#getDocumentsDirectory(com.thomsonreuters.uscl.ereader.common.step.BookStep)}.
     */
    @Test
    public void shouldReturnDocumentsDirectory()
    {
        //given
        given(xppFormatFileSystemImpl.getHtmlPagesDirectory(step))
            .willReturn(new File(temporaryFolder.getRoot(), "workDirectory/Format/05_HtmlPages"));
        //when
        final File file = sut.getDocumentsDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Format/05_HtmlPages"));
    }

    @Test
    public void shouldReturnAssetsDirectory()
    {
        //given
        given(imageFileSystemImpl.getImageDynamicDirectory(step))
            .willReturn(new File(temporaryFolder.getRoot(), "workDirectory/Gather/Images/Dynamic"));
        //when
        final File file = sut.getAssetsDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Gather/Images/Dynamic"));
    }

    @Test
    public void shouldReturnArtwork()
    {
        //given
        given(coverArtUtil.getCoverArt(any(BookDefinition.class)))
            .willReturn(new File("coverArt.PNG"));
        //when
        final File file = sut.getArtwork(step);
        //then
        assertThat(file, hasPath("coverArt.PNG"));
    }
}
