package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static java.util.Arrays.asList;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.isTitle;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListMatchers.titleInfo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListService_GetProviewTitlesTest {
    private static final String VERSION_1 = "v1.0";
    private static final String VERSION_2 = "v1.1";
    private static final String FINAL_STATUS = "Final";
    private static final String REVIEW_STATUS = "Review";
    private static final String REMOVED_STATUS = "Removed";
    private static final String CLEANUP_STATUS = "Cleanup";

    @InjectMocks
    private ProviewTitleListServiceImpl service;
    @Mock
    private BookDefinition book;

    @Test
    public void shouldNotAllowRemovalAndPromotionIfBookNotFound() {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo(VERSION_1, FINAL_STATUS), titleInfo(VERSION_2, REVIEW_STATUS));
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, null);
        // then
        assertThat(titles, hasSize(2));
        assertThat(titles, everyItem(isTitle(false, false, false)));
    }

    @Test
    public void shouldNotAllowPromotionInProgressBook() {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo(VERSION_1, FINAL_STATUS), titleInfo(VERSION_2, REVIEW_STATUS));
        given(book.getPilotBookStatus()).willReturn(PilotBookStatus.IN_PROGRESS);
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles.get(0), isTitle(true, false, false));
        assertThat(titles.get(1), isTitle(true, false, false));
    }


    @Test
    public void shouldNotAllowPromotionOfFinalBook() {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo(VERSION_1, FINAL_STATUS), titleInfo(VERSION_2, REVIEW_STATUS));
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles.get(0), isTitle(true, false, false));
        assertThat(titles.get(1), isTitle(true, true, false));
    }

    @Test
    public void shouldAllowDeletionOfRemoveOrCleanupBook() {
        // given
        final List<ProviewTitleInfo> titleInfos = asList(titleInfo(VERSION_1, REMOVED_STATUS), titleInfo(VERSION_2, CLEANUP_STATUS));
        // when
        final List<ProviewTitle> titles = service.getProviewTitles(titleInfos, book);
        // then
        assertThat(titles.get(0), isTitle(false, false, true));
        assertThat(titles.get(1), isTitle(false, false, true));
    }
}
