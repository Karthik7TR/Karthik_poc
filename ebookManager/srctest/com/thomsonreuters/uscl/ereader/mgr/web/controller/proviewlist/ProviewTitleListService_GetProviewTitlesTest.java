package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.isTitle;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.titleInfo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListService_GetProviewTitlesTest
{
    @InjectMocks
    private ProviewTitleListServiceImpl service;
    @Mock
    private BookTitlesUtil bookTitlesUtil;
    @Mock
    private BookDefinition book;

    @Test
    public void shouldNotAllowRemovalAndPromotionIfBookNotFound()
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Final"), titleInfo("v1.1", "Review"));
        given(bookTitlesUtil.isSplitBook(any(BookDefinition.class), any(Version.class))).willReturn(false);
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, null);
        // then
        assertThat(titles, hasSize(2));
        assertThat(titles, everyItem(isTitle(false, false)));
    }

    @Test
    public void shouldNotAllowPromotionInProgressBook()
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Final"), titleInfo("v1.1", "Review"));
        given(bookTitlesUtil.isSplitBook(eq(book), any(Version.class))).willReturn(false);
        given(book.getPilotBookStatus()).willReturn(PilotBookStatus.IN_PROGRESS);
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles.get(0), isTitle(true, false));
        assertThat(titles.get(1), isTitle(true, false));
    }

    @Test
    public void shouldNotAllowRemovalAndPromotionOfSplitBook()
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Review"));
        given(bookTitlesUtil.isSplitBook(eq(book), any(Version.class))).willReturn(true);
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles, contains(isTitle(false, false)));
    }

    @Test
    public void shouldNotAllowPromotionOfFinalBook()
    {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo("v1.0", "Final"), titleInfo("v1.1", "Review"));
        given(bookTitlesUtil.isSplitBook(eq(book), any(Version.class))).willReturn(false);
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles.get(0), isTitle(true, false));
        assertThat(titles.get(1), isTitle(true, true));
    }
}
