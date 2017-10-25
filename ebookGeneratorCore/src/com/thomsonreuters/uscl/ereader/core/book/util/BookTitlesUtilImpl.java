package com.thomsonreuters.uscl.ereader.core.book.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class BookTitlesUtilImpl implements BookTitlesUtil {
    @Override
    public boolean isSplitBook(@NotNull final BookDefinition book, @NotNull final Version version) {
        Assert.notNull(book);
        Assert.notNull(version);

        //TODO replace with lambda when Java 8 will be available
        final String versionToCompare = version.getVersionWithoutPrefix();
        for (final SplitNodeInfo splitPart : book.getSplitNodes()) {
            final String partVersion = splitPart.getBookVersionSubmitted();
            if (versionToCompare.equals(partVersion)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Set<SplitNodeInfo> getSplitNodeInfosByVersion(
        @NotNull final BookDefinition book,
        @NotNull final Version version) {
        final String versionToCompare = version.getVersionWithoutPrefix();
        final Set<SplitNodeInfo> set = new HashSet<>();

        //TODO replace with lambda when Java 8 will be available
        for (final SplitNodeInfo splitPart : book.getSplitNodes()) {
            final String partVersion = splitPart.getBookVersionSubmitted();
            if (versionToCompare.equals(partVersion)) {
                set.add(splitPart);
            }
        }
        return set;
    }

    @Override
    @NotNull
    public List<BookTitleId> getTitleIds(@NotNull final BookDefinition book, @NotNull final Version version) {
        final String versionToCompare = version.getVersionWithoutPrefix();

        final List<BookTitleId> titleIds = new ArrayList<>();
        titleIds.add(new BookTitleId(book.getFullyQualifiedTitleId(), version));
        for (final SplitNodeInfo splitPart : book.getSplitNodes()) {
            final String partVersion = splitPart.getBookVersionSubmitted();
            if (versionToCompare.equals(partVersion)) {
                titleIds.add(new BookTitleId(splitPart.getSplitBookTitle(), version));
            }
        }
        return titleIds;
    }
}
