package com.thomsonreuters.uscl.ereader.mgr.web.service.proviewlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListServiceImpl;
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
    private BookTitlesUtil bookTitlesUtil;

    @Mock
    private BookDefinition bookDefinition;

    @Mock
    private Version version;

    @Mock
    private SplitDocument splitDocument1;

    @Mock
    private SplitDocument splitDocument2;

    private List<SplitDocument> splitDocuments;
    private String headTitle;
    private String bookPartTitle1;
    private String bookPartTitle2;

    @Before
    public void setUp() {
        proviewTitleListService = new ProviewTitleListServiceImpl(bookDefinitionService, bookTitlesUtil);

        splitDocuments = new ArrayList<>();
        splitDocuments.add(splitDocument1);
        splitDocuments.add(splitDocument2);
        headTitle = "an/uscl/book_title";
        bookPartTitle1 = headTitle + _PT + 2;
        bookPartTitle2 = headTitle + _PT + 3;
    }

    @Test
    public void getAllSplitBookTitleIdsTest() {
        when(bookDefinition.getFullyQualifiedTitleId()).thenReturn(headTitle);
        when(bookDefinition.getSplitDocumentsAsList()).thenReturn(splitDocuments);

        List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIds(bookDefinition, version);

        assertEquals(splitBookTitleIds.size(), splitDocuments.size() + 1);
        assertTrue(splitBookTitleIds.contains(headTitle));
        assertTrue(splitBookTitleIds.contains(bookPartTitle1));
        assertTrue(splitBookTitleIds.contains(bookPartTitle2));
    }
}
