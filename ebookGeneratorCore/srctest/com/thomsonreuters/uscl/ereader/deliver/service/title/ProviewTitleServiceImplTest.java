package com.thomsonreuters.uscl.ereader.deliver.service.title;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleServiceImplTest {
    private static final String DOCS_RESPONSE =
        "<docs><doc id=\"doc1\" src=\"doc1.html\"/><doc id=\"doc2\" src=\"doc2.html\"/></docs>";
    private static final String DOCS_TITLE_ID = "docsTitleId";
    private static final String VERSION_TITLE_ID = "uscl/an/test";
    private static final String TITLE_RESPONSE = "<title apiversion=\"v1\""
        + " version=\"v1.0\""
        + " id=\""
        + VERSION_TITLE_ID
        + "\""
        + " lastupdated=\"20170703\""
        + " language=\"eng\""
        + " status=\"Review\""
        + " onlineexpiration=\"29991231\">"
        + "<material>test</material></title>";

    private ProviewTitleService proviewTitleService;
    @Mock
    private ProviewClient proviewClient;

    @Before
    public void onTestSetUp() throws Exception {
        given(proviewClient.getSingleTitleInfoByVersion(eq(DOCS_TITLE_ID), anyString())).willReturn(DOCS_RESPONSE);
        given(proviewClient.getSinglePublishedTitle(VERSION_TITLE_ID)).willReturn(TITLE_RESPONSE);
        given(proviewClient.getTitleInfo(DOCS_TITLE_ID, "v1.0")).willReturn(DOCS_RESPONSE);

        proviewTitleService = new ProviewTitleServiceImpl(proviewClient);
    }

    @Test
    public void shouldReturnProviewTitleDocs() {
        //given
        final BookTitleId titleId = new BookTitleId(DOCS_TITLE_ID, new Version("v1.0"));
        //when
        final List<Doc> actualDocs = proviewTitleService.getProviewTitleDocs(titleId);
        //then
        final Doc[] expectedDocs = expectedDocs();
        assertThat(actualDocs, hasSize(2));
        assertThat(actualDocs, hasItems(expectedDocs));
    }

    private Doc[] expectedDocs() {
        return new Doc[] {new Doc("doc1", "doc1.html", 0, null), new Doc("doc2", "doc2.html", 0, null)};
    }

    @Test
    public void shouldReturnProviewTitleVersion() {
        //given
        //when
        final Version actualVersion = proviewTitleService.getLatestProviewTitleVersion(VERSION_TITLE_ID);
        //then
        assertThat(actualVersion, equalTo(new Version("v1.0")));
    }
}
