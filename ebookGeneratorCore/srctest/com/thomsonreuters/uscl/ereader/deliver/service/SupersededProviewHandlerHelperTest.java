package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import lombok.Getter;
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
    private static final String TITLE_ID_SINGLE_BOOK = "uscl/an/book_singlebook";
    private static final String TITLE_ID_SPLIT_BOOK = "uscl/an/book_splitbook";
    private static final String TITLE_ID_SPLIT_BOOK_BIG = "uscl/an/book_nvtest_010";
    private static Map<String, ProviewTitleContainer> proviewTitles;

    @InjectMocks
    private SupersededProviewHandlerHelper handler;
    @Mock
    private ProviewClient proviewClient;
    @Mock
    private VersionIsbnService versionIsbnService;

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

        handler.markTitleVersionAsSuperseded(TITLE_ID_SINGLE_BOOK, new Version(baseVersion), proviewTitles);

        verifyChange(TITLE_ID_SINGLE_BOOK, versionToChange);
    }

    @Test
    public void testSplitBook() throws ProviewException {
        final String baseVersion = "v3.0";
        final String versionToChange = "v2.1";

        handler.markTitleVersionAsSuperseded(TITLE_ID_SPLIT_BOOK, new Version(baseVersion), proviewTitles);

        verifyChange(TITLE_ID_SPLIT_BOOK, versionToChange);
        verifyChange(TITLE_ID_SPLIT_BOOK + "_pt2", versionToChange);
        verifyChange(TITLE_ID_SPLIT_BOOK + "_pt3", versionToChange);
    }

    @Test
    public void testSplitBook2BaseVersion2() throws ProviewException {
        final String baseVersion = "v2.0";
        final String versionToChange = "v2.1";
        final String versionToChangePt2 = "v2.1";
        final String titleId = TITLE_ID_SPLIT_BOOK + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook2BaseVersion3() throws ProviewException {
        final String baseVersion = "v3.0";
        final String versionToChange = "v3.2";
        final String versionToChangePt2 = "v2.1";
        final String titleId = TITLE_ID_SPLIT_BOOK + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook2BaseVersion4() throws ProviewException {
        final String baseVersion = "v4.0";
        final String versionToChange = "v3.2";
        final String versionToChangePt2 = "v2.1";
        final String titleId = TITLE_ID_SPLIT_BOOK + "_2";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testSplitBook3() throws ProviewException {
        final String baseVersion = "v2.2";
        final String versionToChange = "v1.4";
        final String versionToChangePt2 = "v1.3";
        final String titleId = TITLE_ID_SPLIT_BOOK + "_3";

        handler.markTitleVersionAsSuperseded(titleId, new Version(baseVersion), proviewTitles);

        verifyChange(titleId, versionToChange);
        verifyChange(titleId + "_pt2", versionToChangePt2);
    }

    @Test
    public void testMarkTitleSuperseded() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        final String titleId = TITLE_ID_SPLIT_BOOK_BIG;

        handler.markTitleSuperseded(titleId, proviewTitles);

        verifyMarkTitleSuperseded();
    }

    private void verifyMarkTitleSuperseded()
        throws IOException, JsonParseException, JsonMappingException, URISyntaxException {
        final ObjectMapper mapper = new ObjectMapper();

        final List<VerificationTitleObject> titleVersions = mapper.readValue(getTestScenarios(), mapper.getTypeFactory().constructCollectionType(List.class, VerificationTitleObject.class));

        titleVersions.forEach(titleVersion ->
            titleVersion.getVersions().forEach(version ->
                verify(proviewClient, times(version.getTimes())).changeTitleVersionToSuperseded(titleVersion.getTitleId(), version.getVersion())
            )
        );
    }

    private void verifyChange(final String titleId, final String version) throws ProviewException {
        verify(proviewClient).changeTitleVersionToSuperseded(titleId, version);
        verify(proviewClient).promoteTitle(titleId, version);
    }

    private static String getAllPublishedTitles() throws IOException, URISyntaxException {
        return FileUtils.readFileToString(new File(SupersededProviewHandlerHelperTest.class.getResource("proviewTitles.xml").toURI()));
    }

    private static String getTestScenarios() throws IOException, URISyntaxException {
        return FileUtils.readFileToString(new File(SupersededProviewHandlerHelperTest.class.getResource("testScenariosSuperseded.json").toURI()));
    }

    @Getter
    private static class VerificationTitleObject {
        private String titleId;
        private List<VerificationVersionObject> versions;
    }

    @Getter
    private static class VerificationVersionObject {
        private String version;
        private int times;
    }
}
