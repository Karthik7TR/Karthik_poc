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
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("proviewTitleListService")
public class ProviewTitleListServiceImpl implements ProviewTitleListService {
    private final BookDefinitionService bookDefinitionService;
    private final BookTitlesUtil bookTitlesUtil;

    @Autowired
    public ProviewTitleListServiceImpl(final BookDefinitionService bookDefinitionService,
                                       final BookTitlesUtil bookTitlesUtil) {
        this.bookDefinitionService = bookDefinitionService;
        this.bookTitlesUtil = bookTitlesUtil;
    }

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

    @SneakyThrows
    @Override
    public List<String> getAllSplitBookTitleIds(final BookDefinition bookDefinition, final Version version) {
        final List<String> splitBookTitles = new ArrayList<>();
        final TitleId titleId = new TitleId(bookDefinition.getFullyQualifiedTitleId());
        final int amountParts = bookDefinition.getSplitDocumentsAsList().size() + 1;
        for (int i = 1; i <= amountParts; i++) {
            splitBookTitles.add(titleId.getPartTitle(i));
        }
        return splitBookTitles;
    }
}
