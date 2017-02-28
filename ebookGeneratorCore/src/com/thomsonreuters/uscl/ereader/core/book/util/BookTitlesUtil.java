package com.thomsonreuters.uscl.ereader.core.book.util;

import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;

/**
 * Util methods for split book titles
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public interface BookTitlesUtil
{
    /**
     * Is specified version of book is split
     * @param book book definition
     * @param version version of book
     * @return split book flag
     */
    boolean isSplitBook(@NotNull BookDefinition book, @NotNull Version version);

    /**
     * Returns split nodes with specified version
     * @param book book definition
     * @param version version of book
     */
    @NotNull
    Set<SplitNodeInfo> getSplitNodeInfosByVersion(@NotNull BookDefinition book, @NotNull Version version);
}
