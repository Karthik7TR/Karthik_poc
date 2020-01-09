package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
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
    private final ProviewHandler proviewHandler;

    @Autowired
    public ProviewTitleListServiceImpl(
        final BookDefinitionService bookDefinitionService,
        final BookTitlesUtil bookTitlesUtil,
        final ProviewHandler proviewHandler) {
        this.bookDefinitionService = bookDefinitionService;
        this.bookTitlesUtil = bookTitlesUtil;
        this.proviewHandler = proviewHandler;
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
    public List<String> getAllSplitBookTitleIdsOnProview(final String headTitle, final Version version,
        final String... titleStatuses) {
        final List<String> splitBookTitles = new ArrayList<>();
        final Set<String> includedStatuses = new HashSet<>(Arrays.asList(titleStatuses));
        final Map<String, ProviewTitleContainer> proviewTitleInfo = proviewHandler.getAllProviewTitleInfo();
        proviewTitleInfo.keySet().stream()
            .filter(title -> headTitle.equals(new TitleId(title).getHeadTitleId()))
            .forEach(title -> proviewTitleInfo.get(title).getProviewTitleInfos().stream()
                .filter(titleInfo -> version.equals(new Version(titleInfo.getVersion())))
                .filter(titleInfo -> includedStatuses.contains(titleInfo.getStatus()))
                .findAny()
                .ifPresent(titleInfo -> splitBookTitles.add(titleInfo.getTitleId())));
        return splitBookTitles;
    }
}
