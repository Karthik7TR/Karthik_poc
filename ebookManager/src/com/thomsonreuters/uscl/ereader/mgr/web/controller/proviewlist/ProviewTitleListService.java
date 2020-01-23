package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProviewTitleListService {
    /**
     * Get list of titles to display on 'Proview list' page
     * @param titleInfos list of titles from ProView
     * @param book book definition
     * @return list of titles to display
     */
    @NotNull
    List<ProviewTitle> getProviewTitles(@NotNull List<ProviewTitleInfo> titleInfos, @Nullable BookDefinition book);

    /**
     * Get book definition of head book
     * @param titleId full title id for volume
     * @return book definition
     */
    @Nullable
    BookDefinition getBook(@NotNull TitleId titleId);

    List<String> getAllSplitBookTitleIdsOnProview(final String headTitle, final Version version,
        final String... titleStatuses);

    TitleActionResult updateTitleStatusesInProview(final ProviewTitleForm form, final Consumer<String> action,
        final String... titleStatuses);

    void promoteTitleOnProview(final ProviewTitleForm form,  final String title);

    void removeTitleFromProview(final ProviewTitleForm form, final String title);

    void deleteTitleFromProview(final ProviewTitleForm form, final String title);

    TitleActionResult executeTitleAction(final ProviewTitleForm form, final TitleAction action,
        final boolean isJobRunningForBook);
}
