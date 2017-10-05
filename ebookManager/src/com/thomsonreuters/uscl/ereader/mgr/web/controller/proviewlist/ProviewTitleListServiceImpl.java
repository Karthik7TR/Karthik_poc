package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Required;

public class ProviewTitleListServiceImpl implements ProviewTitleListService {
    private BookDefinitionService bookDefinitionService;
    private BookTitlesUtil bookTitlesUtil;

    @Override
    @NotNull
    public List<ProviewTitle> getProviewTitles(
        @NotNull final List<ProviewTitleInfo> titleInfos,
        @Nullable final BookDefinition book) {
        final List<ProviewTitle> titles = new ArrayList<>();
        for (final ProviewTitleInfo ti : titleInfos) {
            final Version version = new Version(ti.getVersion());
            final boolean isSingleBook = book == null ? false : !bookTitlesUtil.isSplitBook(book, version);
            final boolean canPromoteBook =
                book == null ? false : isSingleBook && book.getPilotBookStatus() != PilotBookStatus.IN_PROGRESS;
            titles.add(new ProviewTitle(ti, isSingleBook, canPromoteBook));
        }
        return titles;
    }

    @Override
    @Nullable
    public BookDefinition getBook(@NotNull final TitleId titleId) {
        BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(titleId.getTitleId());
        if (bookDef == null) {
            bookDef = bookDefinitionService.findBookDefinitionByTitle(titleId.getHeadTitleId());
        }
        return bookDef;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
    }

    @Required
    public void setBookTitlesUtil(final BookTitlesUtil bookTitlesUtil) {
        this.bookTitlesUtil = bookTitlesUtil;
    }
}
