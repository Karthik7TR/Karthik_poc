package com.thomsonreuters.uscl.ereader.core.book.util;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNodes;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookTitlesUtil_isSplitBookTest {
    @InjectMocks
    private BookTitlesUtilImpl util;

    @Test
    public void shouldReturnTrueForSplitBook() {
        // given
        final BookDefinition book = book("title");
        book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
        // when
        final boolean splitBook = util.isSplitBook(book, version("v1.1"));
        // then
        assertThat(splitBook, is(true));
    }

    @Test
    public void shouldReturnFalseForSingleBook() {
        // given
        final BookDefinition book = book("title");
        book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
        // when
        final boolean splitBook = util.isSplitBook(book, version("v1.2"));
        // then
        assertThat(splitBook, is(false));
    }
}
