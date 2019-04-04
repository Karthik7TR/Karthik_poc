package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.runner.commonsio.FileUtils;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public final class SupersededProviewHandlerHelperTest {
    private static final String titleIdSingleBook = "uscl/an/book_singlebook";
    private static final String titleIdSplitBook = "uscl/an/book_splitbook";
    private static Map<String, ProviewTitleContainer> proviewTitles;

    @InjectMocks
    private SupersededProviewHandlerHelper handler;
    @Mock
    private ProviewClient proviewClient;

    @BeforeClass
    public static void setUpBase() throws IOException, URISyntaxException {
        proviewTitles = new PublishedTitleParser().process(getAllPublishedTitles());
    }

    @Before
    public void init() throws ProviewException {
        when(proviewClient.changeTitleVersionToSuperseded(any(), any())).thenReturn(HttpStatus.OK);
        when(proviewClient.promoteTitle(any(), any())).thenReturn(HttpStatus.OK);
    }

    @Test
    public void testSingleBook() throws ProviewException {
        final String baseVersion = "v2.1";
        final String versionToChange = "v1.3";

        handler.markTitleVersionAsSuperseded(titleIdSingleBook, new Version(baseVersion), proviewTitles);

        verifyChange(titleIdSingleBook, versionToChange);
    }

    @Test
    public void testSplitBook() throws ProviewException {
        final String baseVersion = "v3.0";
        final String versionToChange = "v2.1";

        handler.markTitleVersionAsSuperseded(titleIdSplitBook, new Version(baseVersion), proviewTitles);

        verifyChange(titleIdSplitBook, versionToChange);
        verifyChange(titleIdSplitBook + "_pt2", versionToChange);
        verifyChange(titleIdSplitBook + "_pt3", versionToChange);
    }

    @Test
    public void testSplitBook2BaseVersion2() throws ProviewException {
        final String baseVersion = "v2.0";
        final String versionToChange = "v2.1";
        final String versionToChangePt2 = "v2.1";
        final String titleId = titleIdSplitBook + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook2BaseVersion3() throws ProviewException {
        final String baseVersion = "v3.0";
        final String versionToChange = "v3.2";
        final String versionToChangePt2 = "v2.1";
        final String titleId = titleIdSplitBook + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook2BaseVersion4() throws ProviewException {
        final String baseVersion = "v4.0";
        final String versionToChange = "v3.2";
        final String versionToChangePt2 = "v2.1";
        final String titleId = titleIdSplitBook + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook3() throws ProviewException {
        final String baseVersion = "v2.2";
        final String versionToChange = "v1.4";
        final String versionToChangePt2 = "v1.3";
        final String titleId = titleIdSplitBook + "_3";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    private void verifyChange(final String titleId, final String version) throws ProviewException {
        verify(proviewClient).changeTitleVersionToSuperseded(titleId, version);
        verify(proviewClient).promoteTitle(titleId, version);
    }

    private static String getAllPublishedTitles() throws IOException, URISyntaxException {
        return FileUtils.readFileToString(new File(SupersededProviewHandlerHelperTest.class.getResource("proviewTitles.xml").toURI()));
    }
}
