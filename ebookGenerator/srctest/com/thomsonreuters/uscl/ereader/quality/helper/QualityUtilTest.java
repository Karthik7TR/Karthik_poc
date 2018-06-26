package com.thomsonreuters.uscl.ereader.quality.helper;

import static java.util.Collections.singletonList;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.quality.model.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.model.request.JsonRequest;
import com.thomsonreuters.uscl.ereader.quality.model.response.Report;
import com.thomsonreuters.uscl.ereader.quality.model.response.ReportSourcePair;
import com.thomsonreuters.uscl.ereader.quality.model.response.Response;
import org.junit.Test;

public final class QualityUtilTest {
    private static final String FTP_STORAGE_PATH = "some\\path";
    private static final String EMAILS = "some@email.com";
    private static final String REPORT_FILE_NAME = "report\\file";
    private static final String SOURCE_FILE_NAME = "source\\file.xml";
    private static final ReportSourcePair REPORT_SOURCE_PAIR = new ReportSourcePair(REPORT_FILE_NAME, SOURCE_FILE_NAME);

    private QualityUtil sut = new QualityUtil(FTP_STORAGE_PATH);

    @Test
    public void shouldCreateJsonRequest() throws IOException, URISyntaxException {
        //given
        final CompareUnit compareUnit = new CompareUnit("source", "target");
        final List<CompareUnit> compareUnits = singletonList(compareUnit);
        final String expectedRequestString = readFileToString(new File(QualityUtilTest.class.getResource("request.json").toURI()));
        //when
        final JsonRequest request = sut.createJsonRequest(compareUnits, EMAILS);
        final String actualRequestString = new ObjectMapper().writeValueAsString(request);
        //then
        //trimmed to make sure EOF doesn't break test
        assertEquals(expectedRequestString.trim(), actualRequestString.trim());
    }

    @Test
    public void shouldMapToReportSourcePair() {
        //given
        final Report report = new Report();
        final Response response = new Response();
        report.setReportFile(REPORT_FILE_NAME);
        response.setSourceFile(SOURCE_FILE_NAME);
        //when
        final ReportSourcePair reportSourcePair = sut.mapToReportSourcePair(report, response);
        //then
        assertEquals(REPORT_SOURCE_PAIR, reportSourcePair);
    }

    @Test
    public void shouldGetLocalPath() {
        //given
        final String reportsDirPath = "reports\\dir";
        final String expected = "reports\\dir\\file.html";
        //when
        final String resultLocalPath = sut.getLocalPath(REPORT_SOURCE_PAIR, reportsDirPath);
        //then
        assertEquals(expected, resultLocalPath);
    }
}
