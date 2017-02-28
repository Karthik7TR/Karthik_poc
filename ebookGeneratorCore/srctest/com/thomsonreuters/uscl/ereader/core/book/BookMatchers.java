package com.thomsonreuters.uscl.ereader.core.book;

import java.util.HashSet;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;

public final class BookMatchers
{
    private BookMatchers()
    {
    }

    @NotNull
    public static BookDefinition book(final String titleId)
    {
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(111L);
        bookDefinition.setFullyQualifiedTitleId(titleId);
        return bookDefinition;
    }

    @NotNull
    public static TitleId titleId(final String titleId)
    {
        return new TitleId(titleId);
    }

    @NotNull
    public static Version version(final String version)
    {
        return new Version(version);
    }

    @NotNull
    public static SplitNodeInfo splitNode(final BookDefinition book, final String titleId, final String version)
    {
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(book);
        splitNodeInfo.setSpitBookTitle(titleId);
        splitNodeInfo.setBookVersionSubmitted(version);
        return splitNodeInfo;
    }

    @NotNull
    public static SplitNodeInfo splitNode(
        final BookDefinition book,
        final String titleId,
        final String guid,
        final String version)
    {
        final SplitNodeInfo splitNodeInfo = splitNode(book, titleId, version);
        splitNodeInfo.setSplitNodeGuid(guid);
        return splitNodeInfo;
    }

    @NotNull
    public static Set<SplitNodeInfo> splitNodes(final SplitNodeInfo... nodes)
    {
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        for (final SplitNodeInfo node : nodes)
        {
            splitNodes.add(node);
        }
        return splitNodes;
    }
}
