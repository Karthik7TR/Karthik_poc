package com.thomsonreuters.uscl.ereader.core.book.util;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNodes;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.titleId;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookTitlesUtil_GetTitleIdsTest {
    @InjectMocks
    private BookTitlesUtilImpl util;

    @Test
    public void shouldReturnCorrectListOfBookTitles() {
        // given
        final BookDefinition book = book("title");
        book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
        // when
        final List<BookTitleId> titleIds = util.getTitleIds(book, version("v1.1"));
        // then
        assertThat(titleIds, contains(titleId("title", "v1.1"), titleId("title_pt2", "v1.1")));
    }

    @Test
    public void shouldReturnTitleIdForSingleBook() {
        // given
        final BookDefinition book = book("title");
        // when
        final List<BookTitleId> titleIds = util.getTitleIds(book, version("v1.1"));
        // then
        assertThat(titleIds, contains(titleId("title", "v1.1")));
    }
}
