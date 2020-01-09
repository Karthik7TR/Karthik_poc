package com.thomsonreuters.uscl.ereader.mgr.web.service.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListServiceImpl;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListServiceTest {
    private static final String _PT = "_pt";

    private ProviewTitleListService proviewTitleListService;

    @Mock
    private BookDefinitionService bookDefinitionService;

    @Mock
    private ProviewHandler proviewHandler;

    @Mock
    private BookTitlesUtil bookTitlesUtil;

    private String headTitle;
    private String bookPartTitle1;
    private String bookPartTitle2;
    private String bookPartTitle3;
    private String anotherTitle;
    private Version version;
    private String versionString;
    private String anotherVersion1;
    private String anotherVersion2;
    private Map<String, ProviewTitleContainer> proviewTitleInfo;

    @Before
    public void setUp() {
        proviewTitleListService = new ProviewTitleListServiceImpl(bookDefinitionService, bookTitlesUtil, proviewHandler);

        version = new Version("v2.0");
        versionString = version.getFullVersion();
        anotherVersion1 = "v4.0";
        anotherVersion2 = "v6.0";

        headTitle = "an/uscl/book_title";
        bookPartTitle1 = headTitle + _PT + 2;
        bookPartTitle2 = headTitle + _PT + 3;
        bookPartTitle3 = headTitle + _PT + 4;
        anotherTitle = "uscl/an/another_book";

        setUpProviewTitles();
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forPromote() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REVIEW_BOOK_STATUS);

        assertEquals(1, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(bookPartTitle1));
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forRemove() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS);

        assertEquals(2, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(headTitle));
        assertTrue(splitBookTitleIds.contains(bookPartTitle1));
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forDelete() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REMOVED_BOOK_STATUS, CLEANUP_BOOK_STATUS);

        splitBookTitleIds.forEach(System.out::println);
        assertEquals(2, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(bookPartTitle2));
        assertTrue(splitBookTitleIds.contains(bookPartTitle3));
    }

    @SneakyThrows
    private void setUpProviewTitles() {
        proviewTitleInfo = new HashMap<>();
        setUpHeadTitle();
        setUpPart1();
        setUpPart2();
        setUpPart3();
        setUpAnotherTitle();
        when(proviewHandler.getAllProviewTitleInfo()).thenReturn(proviewTitleInfo);
    }

    private void setUpHeadTitle() {
        final ProviewTitleContainer headTitleContainer = new ProviewTitleContainer();
        addTitleInfo(headTitleContainer, headTitle, versionString, FINAL_BOOK_STATUS);
        addTitleInfo(headTitleContainer, headTitle, anotherVersion1, FINAL_BOOK_STATUS);
        addTitleInfo(headTitleContainer, headTitle, anotherVersion2, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(headTitle, headTitleContainer);
    }

    private void setUpPart1() {
        final ProviewTitleContainer part1Container = new ProviewTitleContainer();
        addTitleInfo(part1Container, bookPartTitle1, versionString, REVIEW_BOOK_STATUS);
        addTitleInfo(part1Container, bookPartTitle1, anotherVersion1, REVIEW_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle1, part1Container);
    }

    private void setUpPart2() {
        final ProviewTitleContainer part2Container = new ProviewTitleContainer();
        addTitleInfo(part2Container, bookPartTitle2, versionString, REMOVED_BOOK_STATUS);
        addTitleInfo(part2Container, bookPartTitle2, anotherVersion1, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle2, part2Container);
    }

    private void setUpPart3() {
        final ProviewTitleContainer part3Container = new ProviewTitleContainer();
        addTitleInfo(part3Container, bookPartTitle3, versionString, CLEANUP_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle3, part3Container);
    }

    private void setUpAnotherTitle() {
        final ProviewTitleContainer anotherTitleContainer = new ProviewTitleContainer();
        addTitleInfo(anotherTitleContainer, anotherTitle, versionString, REVIEW_BOOK_STATUS);
        addTitleInfo(anotherTitleContainer, anotherTitle, anotherVersion1, FINAL_BOOK_STATUS);
        addTitleInfo(anotherTitleContainer, anotherTitle, anotherVersion2, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(anotherTitle, anotherTitleContainer);
    }

    private void addTitleInfo(final ProviewTitleContainer titleContainer, final String title, final String version,
        final String status) {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setTitleId(title);
        titleInfo.setVersion(version);
        titleInfo.setStatus(status);
        titleContainer.getProviewTitleInfos().add(titleInfo);
    }
}
