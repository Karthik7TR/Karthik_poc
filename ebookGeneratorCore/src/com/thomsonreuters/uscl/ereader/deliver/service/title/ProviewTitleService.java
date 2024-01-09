package com.thomsonreuters.uscl.ereader.deliver.service.title;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service to perform requests to proview api, to get title data
 */
public interface ProviewTitleService {
    @Nullable
    Version getLatestProviewTitleVersion(@NotNull String fullyQualifiedTitleId);

    @NotNull
    List<Doc> getProviewTitleDocs(@NotNull BookTitleId titleId);

    @NotNull
    List<BookTitleId> getPreviousTitles(@NotNull Version previousVersion, @NotNull String titleId);

    boolean isMajorVersionPromotedToFinal(@NotNull String fullyQualifiedTitleId, @NotNull Version newVersion);
}
