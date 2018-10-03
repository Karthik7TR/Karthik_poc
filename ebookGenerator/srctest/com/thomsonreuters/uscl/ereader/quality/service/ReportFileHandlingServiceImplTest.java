package com.thomsonreuters.uscl.ereader.quality.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.quality.service.ReportFileHandlingService.ReportFileParameter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public final class ReportFileHandlingServiceImplTest {
    private final ReportFileHandlingServiceImpl sut = new ReportFileHandlingServiceImpl();

    private File testReportFile;

    @Before
    @SneakyThrows
    public void onTestSetUp() {
        testReportFile = new File(getClass().getResource("testReportFile.html").toURI());
    }

    @Test
    public void shouldReturnFileNameFromReportFile() {
        //given
        //when
        final String fileName = sut.extractParameter(testReportFile, ReportFileParameter.FILE_NAME);
        //then
        assertEquals("4-TRG_CACIVP_12(II).main.DIVXML.42086163.transformed", fileName);
    }

    @Test
    public void shouldReturnPercentageFromReportFile() {
        //given
        //when
        final String percentage = sut.extractParameter(testReportFile, ReportFileParameter.PERCENTAGE);
        //then
        assertEquals("100.00000%", percentage);
    }

    @Test
    public void shouldReturnFileNamesMappedToPercentage() {
        //given
        //when
        final Map<String, String> result = sut.getFilesMatchingPercentage(Collections.singletonList(testReportFile));
        //then
        assertThat(result.entrySet(), hasSize(1));
        assertThat(result.keySet(), contains("4-TRG_CACIVP_12(II).main.DIVXML.42086163.transformed"));
        assertThat(result.values(), contains("100.00000%"));
    }
}
