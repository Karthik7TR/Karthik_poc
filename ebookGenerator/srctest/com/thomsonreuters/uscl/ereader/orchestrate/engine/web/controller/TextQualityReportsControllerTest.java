package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TextQualityReportsControllerTest {
    private static final String JOB_ID = "1";
    private static final String MATERIAL = "1111111";

    @InjectMocks
    private TextQualityReportsController sut;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FormatFileSystem formatFileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletResponse mockServletResponse;

    @Before
    public void onTestSetUp() {
        given(formatFileSystem.getFormatDirectory(1L).toPath()
            .resolve(XppFormatFileSystemDir.QUALITY_DIR.getDirName())
            .resolve("1111111")
            .resolve("reports").toFile())
        .willReturn(new File("srctest/com/thomsonreuters/uscl/ereader/orchestrate/engine/web/controller"));
    }

    @Test
    @SneakyThrows
    public void shouldWriteReportToOutputAndStatus200() {
        //given
        //when
        sut.getTextQualityReport(JOB_ID, MATERIAL, "reportFileMock", mockServletResponse);

        //then
        verify(mockServletResponse).setStatus(200);
        verify(mockServletResponse.getOutputStream()).write(any(byte[].class));
    }

    @Test
    @SneakyThrows
    public void shouldWriteErrorMessageToOutputAndStatus410() {
        //given
        //when
        sut.getTextQualityReport(JOB_ID, MATERIAL, "nonexistentFile", mockServletResponse);

        //then
        verify(mockServletResponse).setStatus(410);
        verify(mockServletResponse.getOutputStream()).print("Report file nonexistentFile for job 1 doesn't exist");
    }
}
