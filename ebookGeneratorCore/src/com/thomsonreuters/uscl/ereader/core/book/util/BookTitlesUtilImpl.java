package com.thomsonreuters.uscl.ereader.core.book.util;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class BookTitlesUtilImpl implements BookTitlesUtil
{
    @Override
    public boolean isSplitBook(@NotNull final BookDefinition book, @NotNull final Version version)
    {
        Assert.notNull(book);
        Assert.notNull(version);

        //TODO replace with lambda when Java 8 will be available
        final String versionToCompare = version.getVersionWithoutPrefix();
        for (final SplitNodeInfo splitPart : book.getSplitNodes())
        {
            final String partVersion = splitPart.getBookVersionSubmitted();
            if (versionToCompare.equals(partVersion))
            {
                return true;
            }
        }
        return false;
    }
}
