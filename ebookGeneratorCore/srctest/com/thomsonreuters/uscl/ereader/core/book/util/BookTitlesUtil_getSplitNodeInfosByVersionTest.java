package com.thomsonreuters.uscl.ereader.core.book.util;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNodes;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BookTitlesUtil_getSplitNodeInfosByVersionTest {
    @InjectMocks
    private BookTitlesUtilImpl util;

    @Test
    public void shouldFilterSplitNodeInfosByVersion() {
        // given
        final BookDefinition book = book("title");
        book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
        // when
        final Set<SplitNodeInfo> splitNodeInfosByVersion = util.getSplitNodeInfosByVersion(book, version("v1.1"));
        // then
        assertThat(splitNodeInfosByVersion, contains(splitNode(book, "title_pt2", "1.1")));
    }
}
