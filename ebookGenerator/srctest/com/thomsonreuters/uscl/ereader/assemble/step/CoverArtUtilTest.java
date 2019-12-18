package com.thomsonreuters.uscl.ereader.assemble.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public final class CoverArtUtilTest {
    private static final String BOOK_DEFINITION_COVER = "bdCoverArt.PNG";
    private static final String COVER_FILE_NAME = "coverArt.PNG";
    private static final String DEFAULT_COVER_PATH = "srctest/com/thomsonreuters/uscl/ereader/assemble/step/";
    private static final String STATIC_CONTENT_PATH = "/some/static/content/directory/";

    @InjectMocks
    private CoverArtUtil coverArtUtil;

    @Mock
    private BookDefinition bookDefinition;

    @Before
    public void onTestSetup() throws IllegalAccessException {
        FieldUtils.writeField(coverArtUtil, "defaultCoverPath", DEFAULT_COVER_PATH, true);
        FieldUtils.writeField(coverArtUtil, "coverFileName", COVER_FILE_NAME, true);
        FieldUtils.writeField(coverArtUtil, "staticContentDirectory", new File(STATIC_CONTENT_PATH), true);
    }

    @Test
    public void shouldReturnCoverFileWithNameFromBookDefinition() {
        // given
        given(bookDefinition.getCoverImage()).willReturn(BOOK_DEFINITION_COVER);
        // when
        final File file = coverArtUtil.getCoverArt(bookDefinition);
        // then
        assertThat(file, equalTo(new File(DEFAULT_COVER_PATH + BOOK_DEFINITION_COVER)));
    }

    @Test
    public void shouldReturnDefaultCoverFile() {
        //given
        given(bookDefinition.getCoverImage()).willReturn("some/nonexistent/file.PNG");
        // when
        final File file = coverArtUtil.getCoverArt(bookDefinition);
        // then
        assertThat(file, equalTo(new File(STATIC_CONTENT_PATH + COVER_FILE_NAME)));
    }
}